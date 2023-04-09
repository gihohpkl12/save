package com.movie.security.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * 굳이 등록하지 않아도 spring security에 기본 AuthenticationEntryPoint가 
 * loginForm에서 지정한 url(login)으로 이동시키는 것 같음.
 */
//@Component
//public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{
//
//	@Override
//	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//		RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
//		redirectStrategy.sendRedirect(request, response, "/login");
//	}
//}
