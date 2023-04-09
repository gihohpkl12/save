package com.start.security.authority;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcherEntry;
import org.springframework.security.web.util.matcher.RequestMatcher.MatchResult;
import org.springframework.util.Assert;


import jakarta.servlet.http.HttpServletRequest;

/*
 * RequestMatcherDelegatingAuthorizationManager 소스를 기반으로 수정함.
 * 1) 추가 setMap, clearMap, mapBuild 메소드
 * 2) 수정 check 함수 파라미터 타입(HttpServletRequest -> RequestAuthorizationContext)
 * 2) 수정 builder 패턴 add에 List<AuthorizationContext> 타입으로 추가. (AuthorizationContext도 임의로 생성한 클래스) 
 * (나머지는 RequestMatcherDelegatingAuthorizationManager 클래스 소스를 copy)
 */
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

	private static final AuthorizationDecision DENY = new AuthorizationDecision(false);

	private List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> mappings;

	private CustomAuthorizationManager(
			List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> mappings) {
		Assert.notEmpty(mappings, "mappings cannot be empty");
		this.mappings = mappings;
	}
	
	public void setMap(List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> mappings) {
		this.mappings = mappings;
	}
	
	public void clearMap() {
		this.mappings.clear();
	}
	
	@Override
	public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
		HttpServletRequest request = object.getRequest();
		
		for (RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>> mapping : this.mappings) {
			RequestMatcher matcher = mapping.getRequestMatcher();
			MatchResult matchResult = matcher.matcher(request);
			
			if (matchResult.isMatch()) {
				AuthorizationManager<RequestAuthorizationContext> manager = mapping.getEntry();
				
				return manager.check(authentication,
						new RequestAuthorizationContext(request, matchResult.getVariables()));
			}
		}
		
		return DENY;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {

		private final List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> mappings = new ArrayList<>();

		public Builder add(RequestMatcher matcher, AuthorizationManager<RequestAuthorizationContext> manager) {
			Assert.notNull(matcher, "matcher cannot be null");
			Assert.notNull(manager, "manager cannot be null");
			this.mappings.add(new RequestMatcherEntry<>(matcher, manager));
			return this;
		}
		
		public Builder add(List<AuthorizationContext> authorizationContext) {
			Assert.notNull(authorizationContext, "authorizationContext cannot be null");
			
			for(int i = 0; i < authorizationContext.size(); i++) {
				if(authorizationContext.get(i).getMatcher() != null && authorizationContext.get(i).getManager() != null) {
					System.out.println("add manager "+authorizationContext.get(i).getManager().toString());
					this.mappings.add(new RequestMatcherEntry<>(authorizationContext.get(i).getMatcher(), authorizationContext.get(i).getManager()));
				} else {
					throw new IllegalArgumentException("Mather & manager cannot be null");
				}
			}
			
			return this;
		}

		public Builder mappings(
				Consumer<List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>>> mappingsConsumer) {
			Assert.notNull(mappingsConsumer, "mappingsConsumer cannot be null");
			mappingsConsumer.accept(this.mappings);
			return this;
		}

		public CustomAuthorizationManager build() {
			return new CustomAuthorizationManager(this.mappings);
		}
		
		public List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> mapBuild() {
			return this.mappings;
		}

	}
}
