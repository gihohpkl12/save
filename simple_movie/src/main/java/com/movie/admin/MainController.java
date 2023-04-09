package com.movie.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.movie.account.domain.Account;
import com.movie.account.service.AccountService;
import com.movie.security.role.RoleService;
import com.movie.security.role.domain.Role;
import com.movie.security.url.UrlService;
import com.movie.security.url.domain.Url;

@Controller
@RequestMapping("/admin")
public class MainController {

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private UrlService urlService;
	
	@Autowired
	private RoleService roleService;
	
	@GetMapping
	public String main() {
		return "admin/admin-main";
	}
	
	@GetMapping("/role-manage")
	public String roleManage(Model model) {
		List<Role> roles = roleService.getAllRole();
		model.addAttribute("roles", roles);
		return "admin/role-manage";
	}
	
	@GetMapping("/user-manage")
	public String userManage(Model model) {
		List<Account> users = accountService.getAllAccount();
		model.addAttribute("users", users);
		return "admin/user-manage";
	}
	
	@GetMapping("/url-manage")
	public String resourceManage(Model model) {
		List<Url> urls = urlService.getAllUrl();
		model.addAttribute("urls", urls);
		return "admin/url-manage";
	}
	
	
	
}
