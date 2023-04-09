package com.movie.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.movie.exception.like.LikeException;
import com.movie.like.form.LikeForm;
import com.movie.like.validator.LikeValidator;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LikeController {
	
	@Autowired
	private LikeValidator likeValidator;
	
	@Autowired
	private LikeService likeService;
	
	@InitBinder("likeForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(likeValidator);
    }
	
	@PostMapping("like-enroll")
	@ResponseBody
	public Like enrollLike(@Validated @RequestBody LikeForm likeForm, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			for(ObjectError error : bindingResult.getAllErrors()) {
				log.info("like enroll error "+error.getDefaultMessage());
			}
			return null;
		}
		
		try {
			likeService.enrollLike(likeForm);
		} catch (LikeException e) {
			return null;
		}
		
		return new Like("FOLLOWING");
	}

	@PostMapping("like-delete")
	@ResponseBody
	public Like enrollDelete(@Validated @RequestBody LikeForm likeForm, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			for(ObjectError error : bindingResult.getAllErrors()) {
				log.info("like delete error "+error.getDefaultMessage());
			}
			return null;
		}
		
		try {
			likeService.deleteLike(likeForm);
		} catch (LikeException e) {
			return null;
		}
		
		return new Like("FOLLOW");
	}

	
	@Data
	class Like {
		String like;
		
		public Like(String like) {
			this.like = like;
		}
	}
}
