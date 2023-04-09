package com.movie.util;

import org.springframework.security.core.context.SecurityContextHolder;

import com.movie.account.domain.Account;

public class LoginUserUtil {

	public static String getUserNickname() {
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Account) {
			return ((Account)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNickname();
		}
		
		return "";
	}
	
	public static Account getAccount() {
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Account) {
			return (Account)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		
		return null;
	}
	
	public static boolean isLogin() {
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Account) {
			return true;
		}
		
		return false;
	}
}
