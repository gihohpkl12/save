package com.movie.review.validator;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.movie.account.domain.Account;
import com.movie.review.form.ReviewEnrollForm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewEnrollValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(ReviewEnrollForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ReviewEnrollForm reviewEnrollForm = (ReviewEnrollForm)target;

		try {
			if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Account) {
				Account account = (Account)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				
				if(!account.getNickname().equals(reviewEnrollForm.getNickname())) {
					errors.reject("message", "로그인 후에 리뷰를 등록할 수 있습니다");
				}
			} else {
				errors.reject("message", "로그인 후에 리뷰를 등록할 수 있습니다");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
	}

}
