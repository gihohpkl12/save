package com.movie.like.validator;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.movie.account.domain.Account;
import com.movie.like.form.LikeForm;

@Component
public class LikeValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(LikeForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		LikeForm likeForm = (LikeForm) target;
		
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Account) {
			Account accout = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			if(!likeForm.getNickname().equals(accout.getNickname())) {
				errors.reject("message", "로그인 한 사용자만 좋아요를 누를 수 있습니다");
			}
		}
		
	}

}
