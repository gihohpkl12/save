package com.movie.review.validator;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.movie.account.domain.Account;
import com.movie.review.form.ReviewDeleteForm;

@Component
public class ReviewDeleteValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(ReviewDeleteForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ReviewDeleteForm deleteReviewForm = (ReviewDeleteForm) target;
		
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Account) {
			Account account = (Account)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			if(!deleteReviewForm.getNickname().equals(account.getNickname())) {
				errors.reject("message", "해당 리뷰의 작성자만 삭제할 수 있습니다");
			}
		}
		
	}

}
