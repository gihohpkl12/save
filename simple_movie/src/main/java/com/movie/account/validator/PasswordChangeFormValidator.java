package com.movie.account.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.movie.account.AccountValidatorUtil;
import com.movie.account.form.PasswordChangeForm;

@Component
public class PasswordChangeFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(PasswordChangeForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		PasswordChangeForm passwordChangeFrom = (PasswordChangeForm) target;
		
		if(!AccountValidatorUtil.checkPassword(passwordChangeFrom.getPassword())) {
			errors.reject("message", "비밀번호 양식을 확인해주시기 바랍니다");
		}
		
		if(!AccountValidatorUtil.checkPassword(passwordChangeFrom.getNewPassword())) {
			errors.reject("message", "신규 비밀번호 양식을 확인해주시기 바랍니다");
		}
		
		if(!AccountValidatorUtil.checkPasswordRepeat(passwordChangeFrom.getNewPassword(), passwordChangeFrom.getRepeatPassword())) {
			errors.reject("message", "비밀번호가 서로 다릅니다");
		}
	}

}
