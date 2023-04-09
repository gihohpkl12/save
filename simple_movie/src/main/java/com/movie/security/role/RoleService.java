package com.movie.security.role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.movie.exception.role.RoleException;
import com.movie.security.role.domain.Role;
import com.movie.security.role.form.RoleForm;
import com.movie.security.role.repository.RoleRepository;
import com.movie.security.url.domain.Url;
import com.movie.security.url.repository.UrlRepository;

import jakarta.transaction.Transactional;

@Service
public class RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UrlRepository urlRepository;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<Role> getAllRole() {
		return roleRepository.findAllByOrderByLevelAsc();
	}

	public Role getRoleByRoleId(Long roleId) {
		Optional<Role> role = roleRepository.findById(roleId);

		if (!role.isPresent()) {
			throw new RoleException("해당 Role이 존재하지 않습니다");
		}

		return role.get();
	}

	public Optional<Role> getRoleByRoleName(String roleName) {
		return roleRepository.findByRoleName(roleName);
	}

	@Transactional
	public void check(RoleForm roleForm) {
		Optional<Role> role = roleRepository.findByRoleName(roleForm.getRoleName());

		if (roleForm.isEdit()) {
			if (role.isPresent() && role.get().getId() == roleForm.getId()) {
				update(roleForm, role.get());
			} else if (!role.isPresent()) {
				update(roleForm, role.get());
			} else {
				throw new RoleException("해당 ROLE을 수정할 수 없습니다");
			}
		} else {
			if (role.isPresent()) {
				throw new RoleException("해당 ROLE Name이 이미 존재합니다");
			}
			save(roleForm);
		}
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional
	public void checkBeforeDelete(Long id) {
		Optional<Role> role = roleRepository.findById(id);

		if (role.isPresent()) {
			delete(role.get());
		} else {
			throw new RoleException("해당 ROLE이 존재하지 않습니다");
		}
	}

	/*
	 * roe을 삭제하면 해당 role로 지정되어 있던 url에 값을 null로 변경
	 */
	@Transactional
	private void delete(Role role) {
		List<Url> urls = urlRepository.findAllByRoleId(role.getId());

		for (Url url : urls) {
			if (url.getRole().getId() == role.getId()) {
				url.setRole(null);
			}
		}
		roleRepository.delete(role);
		setLevelOfRolesAfterDelete(role.getLevel());
	}

	@Transactional
	private void setLevelOfRolesAfterDelete(long level) {
		List<Role> roles = (ArrayList<Role>) roleRepository.findAllByOrderByLevelAsc();

		for (Role role : roles) {
			if (role.getLevel() >= level) {
				role.setLevel(role.getLevel() - 1);
			}
		}

	}

	private void save(RoleForm roleForm) {
		setLevelOfRolesBeforeUpdateOrSave(roleForm);
		Role role = new Role(roleForm);
		roleRepository.save(role);
	}

	private void update(RoleForm roleForm, Role role) {
		setLevelOfRolesBeforeUpdateOrSave(roleForm);
		role.setLevel(roleForm.getLevel());
		role.setRoleName(roleForm.getRoleName());
	}

	public Role getNoneRole() {
		Role nonRole = new Role();
		nonRole.setRoleName("ROLE_NONE");

		return nonRole;
	}

	@Transactional
	private void setLevelOfRolesBeforeUpdateOrSave(RoleForm roleForm) {
		HashMap<Long, Integer> levelMap = new HashMap<>();
		ArrayList<Role> roles = (ArrayList<Role>) roleRepository.findAllByOrderByLevelAsc();
		levelMap.put(roleForm.getLevel(), 0);

		for (Role role : roles) {
			if (levelMap.containsKey(role.getLevel())) {
				if (roleForm.getId() != null && roleForm.getId() == role.getId()) {
					continue;
				}
				role.setLevel(role.getLevel() + 1);
				levelMap.put(role.getLevel(), 0);
			}
		}

//		for(int i = 0; i < roles.size(); i++) {
//			if(levelMap.containsKey(roles.get(i).getLevel())) {
//				if(roleForm.getId() != null && roleForm.getId() == roles.get(i).getId()) {
//					continue;
//				}
//				roles.get(i).setLevel(roles.get(i).getLevel()+1);
//				levelMap.put(roles.get(i).getLevel(), 0);
//			}
//		}
	}
}
