package com.movie.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.movie.security.authentication.CustomAuthenticationProvider;
import com.movie.security.authority.AuthorizationManagerContainer;
import com.movie.security.handler.CustomAccessDeniedHandler;
import com.movie.security.handler.CustomAuthenticationFailureHandler;
import com.movie.security.handler.CustomAuthenticationSuccessHandler;
import lombok.extern.slf4j.Slf4j;

/*
 * Spring 6.0부터 
 * authorizeRequests는 deprecated
 * WebSecurity도 deprecated 됨.
 * antMatchers는 사라짐.
 * 
 * 대신
 * authorizeRequests -> authorizeHttpRequests
 * antMatchers -> requestMatchers
 * WebSecurity -> WebSecurityCustomizer
 * 이렇게 사용하면 됨.
 */

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	
	@Autowired
	private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	
	@Autowired
	private AuthorizationManagerContainer authorizationManagerContainer;
	
	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		/*
		 * 이건 시큐리티의 세션 정책
		 * 메인페이지에서 csrf disable 안 하면, search부분에서 csrf 토큰을 생성하려고 하는데 거기서 세션 관련해서 에러가 발생함.
		 * 그래서 이걸 넣어주면 해결됨
		 */
		http
			.sessionManagement()
        	.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
		
		http
			.authorizeHttpRequests((authorize) -> authorize
            .anyRequest().access(authorizationManagerContainer.getCustomAuthorizationManager()));
		
//		http.addFilterBefore(testFilter, UsernamePasswordAuthenticationFilter.class);
		
		http
			.formLogin()
			.loginPage("/login")
			.loginProcessingUrl("/login_proc")
			.successHandler(customAuthenticationSuccessHandler)
			.failureHandler(customAuthenticationFailureHandler)
			.and()
			.logout()
			.logoutUrl("/logout")
			.logoutSuccessUrl("/")
			.permitAll();
		
		http
			.exceptionHandling()
			.accessDeniedHandler(customAccessDeniedHandler);
		
		// 인증, 인가 관련 이전 설정
		/*
		 * http .authorizeHttpRequests() .requestMatchers("/", "/login", "/join",
		 * "/new-account", "/logout", "/search", "/search-result", "/test") .permitAll()
		 * .anyRequest().authenticated();
		 * 
		 * http .formLogin() .loginPage("/login") .loginProcessingUrl("/login_proc")
		 * .permitAll();
		 * 
		 * http. authenticationProvider(new CustomAuthenticationProvider());
		 */
		 
		return http.build();
	}
	
	/*
	 * 인증 무시.
	 */
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/css/**", "/fonts/**", "/img/**", "/js/**", "/sass/**");
	}
	
	/*
	 * 로그인 인증 정보에서 쓰는 provider를 custom해서 생성함
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		log.info("CustomAuthenticationProvider enroll");
		return new CustomAuthenticationProvider();
	}
	
	/* 
	 * AuthorizationManager 관련 이전 코드 기록
	 *  https://github.com/spring-projects/spring-security/issues/9994
	 *  https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html#page-title
	 *  https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html#authz-custom-authorization-manager
	 * 
	 * @Bean
	 * public SecurityFilterChain filterChain(HttpSecurity http, AuthorizationManager<RequestAuthorizationContext> access) throws Exception {
	 * 	http
	 * 		.authorizeHttpRequests((authorize) -> authorize
	 * 		.anyRequest().access(access));
	 * 	}
	 * @Bean 
	 * AuthorizationManager<RequestAuthorizationContext> authz() {
	 * 	RequestMatcher permitAll = authorizationService.getPermitAll();
	 * 	ArrayList<AuthorizationContext> authorizationContext = authorizationService.getAuthorization();
	 * 	log.info("AuthorizationManager enroll");
	 *   //RequestMatcher any = AnyRequestMatcher.INSTANCE;
	 * 
	 * AuthorizationManager<HttpServletRequest> authz = CustomRequestMatcherDelegatingAuthorizationManager.builder()
	 * 		.add(authorizationContext)
	 * 		.add(any, new AuthenticatedAuthorizationManager())
	 * 		.add(permitAll, (authentication, context) -> new AuthorizationDecision(true))
	 * 		.build();
	 * 
	 * 
	 * return (authentication, context) -> authz.check(authentication, context.getRequest());
	 * }
	 */
}
