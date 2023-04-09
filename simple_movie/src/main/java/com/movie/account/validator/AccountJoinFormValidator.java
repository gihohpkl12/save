package com.movie.account.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.movie.account.AccountValidatorUtil;
import com.movie.account.form.AccountJoinForm;

@Component
public class AccountJoinFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(AccountJoinForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		System.out.println("call hi join form");
		AccountJoinForm accountJoinForm = (AccountJoinForm)target;
		
		if(!AccountValidatorUtil.checkPasswordRepeat(accountJoinForm.getPassword(), accountJoinForm.getRepeatPassword())) {
			errors.reject("message", "비밀번호를 확인해주시기 바랍니다");
		}
		
		if(!AccountValidatorUtil.checkEmailPattern(accountJoinForm.getEmail())) {
			errors.reject("message", "이메일 형식이 아닙니다");
		}
	}

}
