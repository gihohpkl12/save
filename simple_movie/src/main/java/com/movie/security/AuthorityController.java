package com.movie.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.movie.security.authority.AuthorizationManagerContainer;
import com.movie.security.hierarchy.RoleHierarchyService;

@Controller
public class AuthorityController {

	@Autowired
	private RoleHierarchyService roleHierarchyService;
	
	@Autowired
	private AuthorizationManagerContainer authorizationManagerContainer;
	
	@GetMapping("authority-reload")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String reload(RedirectAttributes redirectModel, Model model) {
		roleHierarchyService.reload();
		authorizationManagerContainer.reload();
		
		String url = (String)model.getAttribute("next_url");
		if(url != null && !url.equals("")) {
			url = "redirect:/"+url;
			redirectModel.addFlashAttribute("message", (String)model.getAttribute("message"));
		} else {
			url = "admin";
		}

		return url;
	}
}
