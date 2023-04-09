package com.movie.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.movie.exception.review.ReviewException;
import com.movie.review.form.ReviewDeleteForm;
import com.movie.review.form.ReviewEnrollForm;
import com.movie.review.validator.ReviewDeleteValidator;
import com.movie.review.validator.ReviewEnrollValidator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ReviewController {
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private ReviewEnrollValidator reviewEnrollValidator;
	
	@Autowired
	private ReviewDeleteValidator reviewDeleteValidator;
	
	@InitBinder("reviewEnrollForm")
    public void initEnrollBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(reviewEnrollValidator);
    }
	
	@InitBinder("reviewDeleteForm")
	public void initDeleteBinder(WebDataBinder webDataBinder) {
		try {
			webDataBinder.addValidators(reviewDeleteValidator);
		} catch (ReviewException e) {
			log.info(e.getMessage());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@PostMapping("add-review")
	public String addReview(@Validated ReviewEnrollForm reviewEnrollForm, BindingResult bindingResult, RedirectAttributes model, HttpServletRequest request) {
		
		if(bindingResult.hasErrors()) {
			if(bindingResult.getGlobalErrorCount() >= 1) {
				model.addFlashAttribute("message", bindingResult.getGlobalError().getDefaultMessage());
			} else {
				model.addFlashAttribute("message", bindingResult.getFieldError().getDefaultMessage());
			}
			
			return "redirect:"+request.getHeader("Referer");
		}
		
		try {
			reviewService.enrollReview(reviewEnrollForm);
		} catch (ReviewException e) {
			model.addFlashAttribute("message", e.getMessage());
		} catch (Exception e) {
			model.addFlashAttribute("message", "잠시 후에 다시 시도해주시기 바랍니다");
		}
		
		return "redirect:"+request.getHeader("Referer");
	}
	
	@PostMapping("/delete-review")
	public String deleteReview(@Validated ReviewDeleteForm reviewDeleteForm, BindingResult bindingResult, RedirectAttributes model, HttpServletRequest request) {
		if(bindingResult.hasErrors()) {
			if(bindingResult.getGlobalErrorCount() >= 1) {
				model.addFlashAttribute("message", bindingResult.getGlobalError().getDefaultMessage());
			} else {
				model.addFlashAttribute("message", bindingResult.getFieldError().getDefaultMessage());
			}
			
			return "redirect:"+request.getHeader("Referer");
		}
		
		try {
			reviewService.deleteReview(reviewDeleteForm);
		} catch (ReviewException e) {
			model.addFlashAttribute("message", e.getMessage());
		} catch (Exception e) {
			model.addFlashAttribute("message", "잠시 후에 다시 시도해주시기 바랍니다");
		}
		
		return "redirect:"+request.getHeader("Referer");
	}
	
}
