package com.movie.interceptor;

import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.web.servlet.HandlerInterceptor;
import com.movie.util.LoginUserUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordChangeDateCheckInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		boolean passwordChangeDateCheck = false;
		
		if(LoginUserUtil.isLogin()) {
			Cookie[] cookies = request.getCookies();
			
			String cookieName = URLEncoder.encode(LoginUserUtil.getUserNickname()+":passwordLastChangeDate", "UTF-8");
			
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals(cookieName)) {
					LocalDate cur = LocalDate.now();
					LocalDate lastDate = LocalDate.parse(cookie.getValue(), DateTimeFormatter.ISO_DATE);
					
					if(cur.isBefore(lastDate)) {
						passwordChangeDateCheck = true;
					}
				}
			}
		} else {
			return true;
		}
		
		if(!passwordChangeDateCheck) {
			response.sendRedirect("/old-password-change");
			return false;
		}
		
		return true;
	}

}
