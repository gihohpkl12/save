package com.start.security.authority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

import com.start.security.authority.role.RoleHierarchyService;
import com.start.security.authority.url.Url;
import com.start.security.authority.url.UrlRepository;


@Service
public class AuthorizationService {

	@Autowired
	private UrlRepository urlRepository;
	
	@Autowired
	private RoleHierarchyService roleHierarchyService;
	
	public ArrayList<AuthorizationContext> getAuthorization() {
		ArrayList<AuthorizationContext> result = new ArrayList<>();
		Map<String, List<String>> authorizationMap = getAuthorizationUrl();
		
		for(String key : authorizationMap.keySet()) {
			AuthorizationContext context = new AuthorizationContext();
			String role = key;
			
			if(key.indexOf("ROLE_") != -1) {
				String[] temp = key.split("_");
				role = temp[temp.length-1];
			}
			
			AuthorityAuthorizationManager<RequestAuthorizationContext> manager = AuthorityAuthorizationManager.hasRole(role);
			manager.setRoleHierarchy(roleHierarchyService.getRoleHierarchy());
			context.setManager(manager);

			RequestMatcher[] mathchers = new RequestMatcher[authorizationMap.get(key).size()];
			for(int i = 0; i < authorizationMap.get(key).size(); i++) {
				mathchers[i] = new AntPathRequestMatcher(authorizationMap.get(key).get(i));
			}
			
			RequestMatcher matcher = new OrRequestMatcher(mathchers);
			context.setMatcher(matcher);
			result.add(context);
		}
		
		return result;
	}
	
	public RequestMatcher getPermitAll() {
		List<String> permitAllUrl = getPermitAllUrl();
		RequestMatcher[] mathchers = new RequestMatcher[permitAllUrl.size()];
		
		int index = 0;
		for(String url : permitAllUrl) {
			mathchers[index++] = new AntPathRequestMatcher(url); 
		}
		
		return new OrRequestMatcher(mathchers);
	}	
	private Map<String, List<String>> getAuthorizationUrl() {
		Map<String, List<String>> authorizationMap = new HashMap<>();
		List<Url> urls = urlRepository.findAllByRole_idIsNotNullOrderByOrderNumAsc();
		
		for(int i = 0; i < urls.size(); i++) {
			Url url = urls.get(i);
			String role = url.getRole().getRoleName();
			
			if(authorizationMap.containsKey(role)) {
				authorizationMap.get(role).add(url.getUrl());
			} else {
				ArrayList<String> save = new ArrayList<>();
				save.add(url.getUrl());
				authorizationMap.put(role, save);
			}
		}
		
		return authorizationMap;
	}
	
	private List<String> getPermitAllUrl() {
		List<Url> urls = urlRepository.findAllByRole_idIsNull();
		List<String> permitAllUrl = new ArrayList<>();
		 
		for(Url url : urls) {
			permitAllUrl.add(url.getUrl());
		}
		
		return permitAllUrl;
	}
}
