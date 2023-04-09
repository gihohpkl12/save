package com.movie.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.movie.interceptor.PasswordChangeDateCheckInterceptor;

/*
 * 비밀번호 변경 알림 기능
 * 쿠키에 저장된 값 기준으로 비밀번호 변경 후 30일이 지났으면 비빌번호 변경.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new PasswordChangeDateCheckInterceptor())
			.order(1)
			.addPathPatterns("/")
			.excludePathPatterns("/css/**", "/error", "*.ico", "/img/**");
	}
}
