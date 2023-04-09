package com.start.security.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.start.jwt.JwtTokenService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	JwtTokenService jwtTokenService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = jwtTokenService.extractTokenFromRequest(request);
		String refreshToken = jwtTokenService.extractRefreshTokenFromRequest(request);

		try {
			if(token != null && !token.equals("")) {
				Authentication authentication = null;
				// 로그아웃 x
				if(!jwtTokenService.isLogout(token)) {
					// 토큰이 만료됨
					if(jwtTokenService.isTokenExpired(token)) {
						if(refreshToken != null && !refreshToken.equals("") && jwtTokenService.validateRefreshTokenProcess(refreshToken)) {
							token = jwtTokenService.createAccessToken(jwtTokenService.getNickname(refreshToken), jwtTokenService.getRole(refreshToken));
							response.setHeader("JWT", token);
							authentication = jwtTokenService.createAuthentication(token);
						}
					} else {
						// 토큰이 만료되지 않음
						if(jwtTokenService.validateTokenProcess(token)) {
							authentication = jwtTokenService.createAuthentication(token);
						}
					}

					if(authentication != null) {
						System.out.println("not null");
						SecurityContextHolder.getContext().setAuthentication(authentication);
						System.out.println(SecurityContextHolder.getContext().toString());
					}
				}

			}
		} catch (SecurityException | MalformedJwtException e) {
			log.info("invaild token");
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported token");
		} catch (IllegalArgumentException e) {
			log.info("claims String is empty");
		} catch (ExpiredJwtException e) {
			log.info("token expired");
		} finally {
			filterChain.doFilter(request, response);
		}
	}
}
