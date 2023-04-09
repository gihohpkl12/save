package com.movie.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import com.movie.security.hierarchy.RoleHierarchyService;

@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
	
	@Autowired
	private RoleHierarchyService roleHierarchyService;

	@Bean
	public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		handler.setRoleHierarchy(roleHierarchyService.getRoleHierarchy());
		return handler;
	}
	
	/*
	 * Authority에서 default prefix(='ROLE_') 말고 custom 생성 가능.
	 */
//	@Bean
//	static GrantedAuthorityDefaults grantedAuthorityDefaults() {
//		return new GrantedAuthorityDefaults("");
//	}
}

/*
 * Configuration
@RequiredArgsConstructor
public class DenyMethodSecurityConfig {
    
    private final ApplicationContext applicationContext;

    @Bean
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        CustomMethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
        expressionHandler.setApplicationContext(applicationContext);
        return expressionHandler;
    }
}

 */
