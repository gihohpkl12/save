package com.movie.security.hierarchy;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Service;
import com.movie.security.role.domain.Role;
import com.movie.security.role.repository.RoleRepository;

@Service
public class RoleHierarchyService {

	@Autowired
	private RoleRepository roleRepository;
	
	private RoleHierarchyImpl hierarchy;
	
	synchronized public RoleHierarchy getRoleHierarchy() {
		if(hierarchy == null) {
			setRoleHierarchy();
		}
		
		return hierarchy;
	}
	
	public void reload() {
		setRoleHierarchy();
	}
	
	private void setRoleHierarchy() {
		ArrayList<Role> roles = (ArrayList<Role>) roleRepository.findAllByOrderByLevelAsc();
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < roles.size()-1; i++) {
			sb.append(roles.get(i).getRoleName());
			sb.append(" > ");
			sb.append(roles.get(i+1).getRoleName());
			sb.append("\n");
		}
		
		RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
		hierarchy.setHierarchy(sb.toString());
		this.hierarchy = hierarchy;
		
	}	
}
