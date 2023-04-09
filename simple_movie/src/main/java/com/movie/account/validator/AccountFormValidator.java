package com.movie.account.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.movie.account.AccountValidatorUtil;
import com.movie.account.form.AccountForm;

@Component
public class AccountFormValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(AccountForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AccountForm accountForm = (AccountForm) target;
		
		if(!AccountValidatorUtil.checkEmailPattern(accountForm.getEmail())) {
			errors.reject("message", "이메일 형식이 아닙니다");
		}
	}
}
