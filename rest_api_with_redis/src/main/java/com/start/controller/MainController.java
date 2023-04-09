package com.start.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.start.account.AccountService;
import com.start.account.form.JoinForm;
import com.start.account.form.LoginForm;
import com.start.account.validator.JoinFormValidator;
import com.start.exception.AccountException;
import com.start.jwt.JwtTokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.json.JSONObject;

@RestController
public class MainController {

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private JoinFormValidator joinFormValidator;
	
	@Autowired
	private JwtTokenService jwtTokenService;
	
	@InitBinder("joinForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(joinFormValidator);
    }
	
	@PostMapping("join")
	public JSONObject join(JoinForm joinForm, BindingResult bindingResult, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		
		if(bindingResult.hasErrors()) {
			result.put("join fail", bindingResult.getGlobalError().getDefaultMessage());
			return result;
		}
		
		try {
			accountService.join(joinForm);
		} catch (AccountException e) {
			result.put("error", e.getMessage());
			return result;
		} catch (Exception e) {
			result.put("error", e.getMessage());
			return result;
		}
		
		result.put("result", "join success");
		return result;
	}
	
	@PostMapping("login")
	public JSONObject login(LoginForm loginForm, BindingResult bindingResult, HttpServletResponse response, HttpSession session) {
		JSONObject result = new JSONObject();
		
		if(bindingResult.hasErrors()) {
			result.put("error",  bindingResult.getFieldError().getDefaultMessage());
			return result;
		}
		
		try {
			accountService.login(loginForm, response);
			Cookie cookie = new Cookie("JWT", response.getHeader("JWT"));
			response.addCookie(cookie);
			result.put("result", "login success");
		} catch (AccountException e) {
			result.put("error",  bindingResult.getFieldError().getDefaultMessage());
			return result;
		}
		
		return result;
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping("logout")
	public JSONObject logout(HttpServletRequest request)  {
		JSONObject result = new JSONObject();
		String accessToken = request.getHeader("JWT");
		String refreshToken = request.getHeader("JWT-REFRESH");
		String nickname = SecurityContextHolder.getContext().getAuthentication().getName();
		
		try {
			jwtTokenService.logout(accessToken, refreshToken, nickname);
			result.put("logout-success", "로그아웃에 성공했습니다");
		} catch (AccountException e) {
			result.put("error", e.getMessage());
			return result;
		} catch (Exception e) {
			result.put("error", e.getMessage());
			return result;
		}
		
		return result;
	}
}
