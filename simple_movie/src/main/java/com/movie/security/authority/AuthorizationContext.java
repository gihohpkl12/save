package com.movie.security.authority;

import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcher;

import lombok.Data;

/*
 * CustomRequestMatcherDelegatingAuthorizationManager에서
 * list형태로 RequestMatcher와 AuthorizationManager<RequestAuthorizationContext>를 저장할 때, 편리성을 위해서 생성한 클래스.
 */
@Data
public class AuthorizationContext {
	
	private RequestMatcher matcher;
	private AuthorizationManager<RequestAuthorizationContext> manager;

}
