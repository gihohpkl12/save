package com.start.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;

import com.start.security.authority.AuthorizationManagerContainer;
import com.start.security.filter.CustomAuthenticationFilter;
import com.start.security.handler.CustomAccessDeniedHandler;
import com.start.security.handler.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;
	@Autowired
	private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	@Autowired
	private CustomAuthenticationFilter customAuthenticationFilter;
	@Autowired
	private AuthorizationManagerContainer authorizationManagerContainer;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
		
		http.logout().disable();
		
		http.httpBasic().disable();
		
		http.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
//		http.authorizeHttpRequests()
//			.requestMatchers("/login", "/join", "/")
//			.permitAll()
//			.anyRequest()
//			.authenticated();
		
		http
		.authorizeHttpRequests((authorize) -> authorize
        .anyRequest().access(authorizationManagerContainer.getCustomAuthorizationManager()));
		
		http.addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//		http.addFilterAfter(customAuthenticationFilter, SecurityContextHolderFilter.class);
		
		http.exceptionHandling()
			.authenticationEntryPoint(customAuthenticationEntryPoint)
			.accessDeniedHandler(customAccessDeniedHandler);
		
		return http.build();
	}
}
