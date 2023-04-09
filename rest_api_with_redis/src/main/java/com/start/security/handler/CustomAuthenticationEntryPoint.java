package com.start.security.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * 인증 관련 핸들러
 * 
 * 인증이 필요한 자원에 사용자가 인증 없이 접근 한 경우에
 * (ex. login하지 않고 특정 login이 필요한 자원에 접근한 경우)
 * 작동하는 필터
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		System.out.println("CustomAuthenticationEntryPoint call : un-authorized");
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UnAuthorized");
	}

}
