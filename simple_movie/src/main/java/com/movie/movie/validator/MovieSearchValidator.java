package com.movie.movie.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.movie.movie.form.MovieSearchForm;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MovieSearchValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(MovieSearchForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		MovieSearchForm movieSearchForm = (MovieSearchForm)target;
		
		if((movieSearchForm.getActor() == null || movieSearchForm.getActor().equals("")) &&
			(movieSearchForm.getTitle() == null || movieSearchForm.getTitle().equals("")) &&
			(movieSearchForm.getDirector() == null || movieSearchForm.getDirector().equals(""))) {
			errors.reject("message", "검색어를 입력해주세요");
//			errors.rejectValue("error", "error", "검색어를 입력해주세요");
		} 
			
		
	}

}
