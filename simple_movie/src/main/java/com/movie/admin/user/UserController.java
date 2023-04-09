package com.movie.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.movie.account.domain.Account;
import com.movie.account.form.AccountForm;
import com.movie.account.form.AccountJoinForm;
import com.movie.account.service.AccountService;
import com.movie.account.validator.AccountFormValidator;
import com.movie.exception.account.AccountException;
import com.movie.security.role.RoleService;
import com.movie.security.role.domain.Role;

@Controller
@RequestMapping("admin/user")
public class UserController {
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private AccountFormValidator accountFormValidator;
	
	@InitBinder("accountForm")
	public void initPasswordChangeFormBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(accountFormValidator);
    }

	@GetMapping("{nickname}")
	public String userDetail(@PathVariable String nickname,  Model model, RedirectAttributes redirectModel) {
		Account account = accountService.getAccount(nickname);
		List<Role> roles = roleService.getAllRole();
		
		model.addAttribute("user", account);
		model.addAttribute("accountForm", new AccountForm());
		model.addAttribute("roles", roles);
		return "admin/user/user-detail";
	}
	
	@PostMapping("user-edit") 
	public String editUser(AccountForm accountForm, BindingResult bindingResult, RedirectAttributes redirectModel) {
		if(bindingResult.hasErrors()) {
			redirectModel.addFlashAttribute("message", getErrorMessages(bindingResult));
			return "redirect:/admin/user-manage";
		}
		
		try {
			accountService.checkBeforeEdit(accountForm);
		} catch (AccountException e) {
			redirectModel.addFlashAttribute("message", e.getMessage());
			return "redirect:/admin/user-manage";
		} catch (Exception e) {
			redirectModel.addFlashAttribute("message", "잠시 후에 다시 시도해주시기 바랍니다");
			return "redirect:/admin/user-manage";
		}
		return "redirect:/admin/user-manage";
	}
	
	@GetMapping("register")
	public String userRegister(Model model) {
		List<Role> roles = roleService.getAllRole();
		model.addAttribute("roles", roles);
		model.addAttribute("accountForm", new AccountJoinForm());
		return "admin/user/user-detail";
		
	}
	
	@PostMapping("user-delete")
	public String deleteUser(Long id, RedirectAttributes redirectModel) {
		if(id == null) {
			redirectModel.addFlashAttribute("message", "id가 null입니다");
			return "redirect:/admin/user-manage";
		}
		
		try {
			accountService.checkBeforeDeleteByAdmin(id);
		} catch (AccountException e) {
			redirectModel.addFlashAttribute("message", e.getMessage());
			return "redirect:/admin/user-manage";
		} catch (Exception e) {
			redirectModel.addFlashAttribute("message", "잠시 후에 다시 시도해주시기 바랍니다");
			return "redirect:/admin/user-manage";
		}
		
		return "redirect:/admin/user-manage";
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
