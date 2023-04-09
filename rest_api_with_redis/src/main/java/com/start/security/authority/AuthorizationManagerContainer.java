package com.start.security.authority;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

/*
 * CustomAuthorizationManager를 관리할 Container 클래스
 * 
 * 인가 정보 동적 변동을 반영하기 위해 생성함. 
 */

@Component
public class AuthorizationManagerContainer {

	private CustomAuthorizationManager customAuthorizationManager;
	
	@Autowired
	private AuthorizationService authorizationService;
	
	public CustomAuthorizationManager getCustomAuthorizationManager() {
		if(this.customAuthorizationManager == null) {
			setCustomTest();
		}
		return this.customAuthorizationManager;
	}
	
	public void setCustomTest() {
		RequestMatcher permitAll = authorizationService.getPermitAll();
		ArrayList<AuthorizationContext> authorizationContext = authorizationService.getAuthorization();
		
		customAuthorizationManager = CustomAuthorizationManager.builder()
	        .add(authorizationContext)
	        .add(permitAll, (authentication, context) -> new AuthorizationDecision(true))
	        .build();
		
	}
	
	public void reload() {
		customAuthorizationManager.clearMap();
		
		RequestMatcher permitAll = authorizationService.getPermitAll();
		ArrayList<AuthorizationContext> authorizationContext = authorizationService.getAuthorization();
		
		customAuthorizationManager.setMap(CustomAuthorizationManager.builder()
		        .add(authorizationContext)
		        .add(permitAll, (authentication, context) -> new AuthorizationDecision(true))
		        .mapBuild());
	}
}
