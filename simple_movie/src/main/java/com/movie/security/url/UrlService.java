package com.movie.security.url;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.movie.account.domain.Account;
import com.movie.exception.role.RoleException;
import com.movie.exception.url.UrlException;
import com.movie.security.role.RoleService;
import com.movie.security.role.domain.Role;
import com.movie.security.role.repository.RoleRepository;
import com.movie.security.url.domain.Url;
import com.movie.security.url.form.UrlForm;
import com.movie.security.url.repository.UrlRepository;

@Service
public class UrlService {
	
	@Autowired
	private UrlRepository urlRepository;
	
	@Autowired
	private RoleService roleService;
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<Url> getAllUrl() {
		return urlRepository.findAll();
	}
	
	public Url getUrl(Long id) {
		Optional<Url> url = urlRepository.findById(id);
		if(!url.isPresent()) {
			throw new UrlException("해당 url이 없습니다");
		}
		 return url.get();
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void checkBeforeDelete(Long id) {
		Optional<Url> url = urlRepository.findById(id);
		
		if(url.isPresent()) {
			delete(url.get());
		} else {
			throw new UrlException("해당 url이 존재하지 않습니다");
		}
	}
	
//	@PreAuthorize("hasRole('ROLE_ADMIN')")
//	@Transactional
//	public void deleteRoleOfUrl(Long id) {
//		
//	}
	
	private void delete(Url url) {
		urlRepository.delete(url);
	}
	
	@Transactional
	public void check(UrlForm urlForm) {
		try {
			Optional<Role> role = null;
			
			if(!urlForm.getRoleName().equals("ROLE_NONE")) {
				role = roleService.getRoleByRoleName(urlForm.getRoleName());
				
				if(role == null || !role.isPresent()) {
					throw new RoleException("존재하지 않는 ROLE 입니다");
				}
			}
			
			Optional<Url> urlForNameCheck = urlRepository.findByUrl(urlForm.getUrl());
			if(urlForNameCheck.isPresent() && urlForm.isEdit()) {
				Optional<Url> originUrl = urlRepository.findById(urlForm.getId());
				
				if(urlForNameCheck.get().getId() == originUrl.get().getId()) {
					update(urlForm, originUrl, role);
				} else {
					throw new UrlException("이미 존재하는 URL입니다");
				}
			} else if(!urlForNameCheck.isPresent() && urlForm.isEdit()){
				Optional<Url> originUrl = urlRepository.findById(urlForm.getId());
				update(urlForm, originUrl, role);
			} else if(!urlForNameCheck.isPresent() && !urlForm.isEdit()) {
				save(urlForm, role);
			} else {
				throw new UrlException("등록할 수 없는 URL입니다");
			}
		} catch (UrlException e) {
			throw new UrlException("이미 존재하는 URL입니다");
		} catch (RoleException e) {
			throw new UrlException(e.getMessage());
		} catch (Exception e) {
			throw new UrlException("이미 존재하는 URL입니다");
		}
	}
	
	private void save(UrlForm urlForm, Optional<Role> role) {
		Url url;
		if(urlForm.getRoleName().equals("ROLE_NONE")) {
			url = new Url(urlForm);
		} else {
			url = new Url(urlForm, role.get());
		}
		urlRepository.save(url);
	}

	private void update(UrlForm urlEnrollForm, Optional<Url> originUrl, Optional<Role> role) {
		try {
			
			if(!urlEnrollForm.getRoleName().equals("ROLE_NONE")) {
				originUrl.get().setRole(role.get());
			} else {
				originUrl.get().setRole(null);
			}
			originUrl.get().setUrl(urlEnrollForm.getUrl());
			originUrl.get().setOrderNum(urlEnrollForm.getOrderNum());
			
		} catch (RoleException e) {
			throw new RoleException(e.getMessage());
		} catch (Exception e) {
			throw new RoleException(e.getMessage());
		}
	}
	

}
