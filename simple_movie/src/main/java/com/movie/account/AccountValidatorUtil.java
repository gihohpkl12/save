package com.movie.account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountValidatorUtil {
	
	public static boolean checkEmailPattern(String email) {
		String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
		Pattern pattern = Pattern.compile(regex);
		
		Matcher matcher = pattern.matcher(email);
		if(matcher.matches()) {
			return true;
		}
		
		return false;
	}
	
	public static boolean checkPassword(String password) {
		if(password == null || password.length() > 4 || password.length() == 0) {
			return false;
		}
		
		String reget = "^[0-9]*$";
		Pattern pattern = Pattern.compile(reget);
		
		Matcher matcher = pattern.matcher(password);
		if(!matcher.matches()) {
			return false;
		}
		
		return true;
	}
	
	public static boolean checkPasswordRepeat(String password, String repeatPassword) {
		if(password.equals(repeatPassword)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean checkPasswordAndNewPassword(String password, String newPassword) {
		if(password.equals(newPassword)) {
			return false;
		}
		
		return true;
	}
	
}
