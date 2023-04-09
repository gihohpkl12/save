package com.movie.admin.url;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.movie.exception.role.RoleException;
import com.movie.exception.url.UrlException;
import com.movie.security.role.RoleService;
import com.movie.security.role.domain.Role;
import com.movie.security.url.UrlService;
import com.movie.security.url.domain.Url;
import com.movie.security.url.form.UrlForm;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("admin/url")
public class UrlController {
	
	@Autowired
	private UrlService urlService;
	
	@Autowired
	private RoleService roleService;
	
	final private String URL_ROLE_RELOAD_REDIRECT_URL = "redirect:http://localhost:8080/authority-reload"; 
	
	@GetMapping("{id}")
	public String urlDetail(@PathVariable Long id, Model model, RedirectAttributes redirectModel) {
		try {
			Url url = urlService.getUrl(id);
			ArrayList<Role> roles = (ArrayList<Role>) roleService.getAllRole();
			roles.add(roleService.getNoneRole());
			
			if(url.getRole() == null) {
				url.setRole(roleService.getNoneRole());
			}
			
			model.addAttribute("url", url);
			model.addAttribute("roles", roles);
			model.addAttribute("urlForm", new UrlForm());
			
		} catch (UrlException e) {
			redirectModel.addFlashAttribute("message", e.getMessage());
			return "redirect:/admin/url-manage";
		} catch (Exception e) {
			redirectModel.addFlashAttribute("message", "잠시 후에 다시 시도해주시기 바랍니다");
			return "redirect:/admin/url-manage";
		}
		return "admin/url/url-detail";
	}
	
	@PostMapping("url-edit")
	public String editUrl(UrlForm urlForm, BindingResult bindingResult, RedirectAttributes redirectModel) {
		if(bindingResult.hasErrors()) {
			redirectModel.addFlashAttribute("message", getErrorMessages(bindingResult));
			return "redirect:/admin/url-manage";
		}
		
		try {
			urlForm.setEdit(true);
			urlService.check(urlForm);
		} catch (UrlException e) {
			redirectModel.addFlashAttribute(e.getMessage());
			return "redirect:/admin/url-manage";
		} catch (RoleException e) {
			redirectModel.addFlashAttribute(e.getMessage());
			return "redirect:/admin/url-manage";
		}
		
		redirectModel.addFlashAttribute("message", "수정 됐습니다");
		redirectModel.addFlashAttribute("next_url", "admin/url-manage");
		return URL_ROLE_RELOAD_REDIRECT_URL;
	}
	
	@PostMapping("url-enroll")
	public String enrollUrl(UrlForm urlForm, BindingResult bindingResult, RedirectAttributes redirectModel, HttpServletRequest request) {
		if(bindingResult.hasErrors()) {
			redirectModel.addFlashAttribute("message", getErrorMessages(bindingResult));
			return "redirect:/admin/url-manage";
		}
		
		try {
			urlForm.setEdit(false);
			urlService.check(urlForm);
		} catch (UrlException e) {
			redirectModel.addFlashAttribute(e.getMessage());
			return "redirect:/admin/url-manage";
		} catch (RoleException e) {
			redirectModel.addFlashAttribute(e.getMessage());
			return "redirect:/admin/url-manage";
		}
		
		redirectModel.addFlashAttribute("message", "등록 됐습니다");
		redirectModel.addFlashAttribute("next_url", "admin/url-manage");
		return URL_ROLE_RELOAD_REDIRECT_URL;
	}
	
	@GetMapping("register")
	public String enrollUrl(Model model) {
		ArrayList<Role> roles = (ArrayList<Role>) roleService.getAllRole();
		roles.add(roleService.getNoneRole());
		model.addAttribute("roles", roles);
		model.addAttribute("urlForm", new UrlForm());
		
		return "admin/url/url-detail";
	}
	
	@PostMapping("url-delete")
	public String deleteUrl(Long id, RedirectAttributes redirectModel) {
		if(id == null) {
			redirectModel.addFlashAttribute("message", "id가 null입니다");
			return "redirect:/admin/url-manage";
		}
		
		try {
			urlService.checkBeforeDelete(id);
			redirectModel.addFlashAttribute("next_url", "admin/url-manage");
			redirectModel.addFlashAttribute("message", "삭제 됐습니다");
		} catch (UrlException e) {
			redirectModel.addFlashAttribute("message", e.getMessage());
			return "redirect:/admin/url-manage";
		} catch (Exception e) {
			redirectModel.addFlashAttribute("message", "잠시 후에 다시 시도해주시기 바랍니다");
			return "redirect:/admin/url-manage";
		}
		
		return URL_ROLE_RELOAD_REDIRECT_URL;
	}
	
	private String getErrorMessages(BindingResult bindingResult) {
		StringBuilder sb = new StringBuilder();
		
		for(ObjectError error : bindingResult.getAllErrors()) {
			sb.append(error.getDefaultMessage());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
