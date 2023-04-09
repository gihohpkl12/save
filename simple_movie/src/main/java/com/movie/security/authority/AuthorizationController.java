package com.movie.security.authority;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthorizationController {

	@GetMapping("/permission-error")
	public String error(Model model) {
		model.addAttribute("timestamp", LocalDate.now());
		return "error";
	}
}
