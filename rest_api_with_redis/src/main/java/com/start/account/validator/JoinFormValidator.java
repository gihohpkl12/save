package com.start.account.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.start.account.form.JoinForm;

@Component
public class JoinFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(JoinForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		JoinForm join = (JoinForm) target;
		
		if(!join.getPassword().equals(join.getPasswordRepeat())) {
			errors.reject("message", "비밀번호가 서로 다릅니다");
		}
	}

}
