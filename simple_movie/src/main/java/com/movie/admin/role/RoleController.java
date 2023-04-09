package com.movie.admin.role;

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
import com.movie.security.role.RoleService;
import com.movie.security.role.domain.Role;
import com.movie.security.role.form.RoleForm;


@Controller
@RequestMapping("admin/role")
public class RoleController {

	@Autowired
	private RoleService roleService;
	
	final private String URL_ROLE_RELOAD_REDIRECT_URL = "redirect:http://localhost:8080/authority-reload"; 
	
//	final private String REDIREDT_URL = "redirect:/";
//	final private String ADMIN_URL = "admin";
//	final private String ROLE_URL = "role";
//	final private String ADMIN_ROLE_URL = "admin/role";
//	final private String ROLE_MANAGE_URL = "role-manage";
//	final private String ROLE_DETAIL_URL = "role-detail";
	
	@GetMapping("{id}")
	public String urlDetail(@PathVariable Long id, Model model, RedirectAttributes redirectModel) {
		try {
			Role role = roleService.getRoleByRoleId(id);
			model.addAttribute("role", role);
			model.addAttribute("roleForm", new RoleForm());
		} catch (RoleException e) {
			redirectModel.addFlashAttribute("message", e.getMessage());
			return "redirect:/admin/role-manage";
		}
		
		return "admin/role/role-detail";
	}
	
	@PostMapping("role-edit")
	public String edidRole(RoleForm roleForm, BindingResult bindingResult, RedirectAttributes redirectModel) {
		if(bindingResult.hasErrors()) {
			redirectModel.addFlashAttribute("message", getErrorMessages(bindingResult));
			return "redirect:/admin/role-manage";
		}
		
		roleForm.setEdit(true);
		roleService.check(roleForm);
		redirectModel.addFlashAttribute("next_url", "admin/role-manage");
		redirectModel.addFlashAttribute("message", "수정 됐습니다");
		return URL_ROLE_RELOAD_REDIRECT_URL;
	}
	
	@PostMapping("role-enroll")
	public String enrollRole(RoleForm roleForm, BindingResult bindingResult, RedirectAttributes redirectModel) {
		if(bindingResult.hasErrors()) {
			redirectModel.addFlashAttribute("message", getErrorMessages(bindingResult));
			return "redirect:/admin/role-manage";
		}
		
		try {
			roleForm.setEdit(false);
			roleService.check(roleForm);
			redirectModel.addFlashAttribute("next_url", "admin/role-manage");
			redirectModel.addFlashAttribute("message", "등록 됐습니다");
		} catch (RoleException e) {
			redirectModel.addFlashAttribute("message", e.getMessage());
			return "redirect:/admin/role-manage";
		} catch (Exception e) {
			redirectModel.addFlashAttribute("message", "잠시 후에 다시 시도해주시기 바랍니다");
			return "redirect:/admin/role-manage";
		}
		return URL_ROLE_RELOAD_REDIRECT_URL;
	}
	
	@GetMapping("register")
	public String registRole(Model model) {
		model.addAttribute("roleForm", new RoleForm());
		return "admin/role/role-detail";
	}
	
	@PostMapping("role-delete")
	public String deleteRole(Long id, RedirectAttributes redirectModel) {
		if(id == null) {
			redirectModel.addFlashAttribute("message", "삭제 할 수 없습니다");
			return "redirect:/admin/role-manage";
		}
		
		try {
			roleService.checkBeforeDelete(id);
			redirectModel.addFlashAttribute("next_url", "admin/role-manage");
			redirectModel.addFlashAttribute("message", "삭제 됐습니다");
		} catch (RoleException e) {
			redirectModel.addFlashAttribute("message", e.getMessage());
			return "redirect:/admin/role-manage";
		} catch (Exception e) {
			redirectModel.addFlashAttribute("message", "잠시 후에 다시 시도해주시기 바랍니다");
			return "redirect:/admin/role-manage";
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
