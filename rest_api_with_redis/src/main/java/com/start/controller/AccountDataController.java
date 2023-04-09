package com.start.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.start.account.Account;
import com.start.account.AccountRepository;
import com.start.account.AccountService;

import net.sf.json.JSONObject;

@RestController
public class AccountDataController {

	@Autowired
	private AccountService accountService;
	
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	@GetMapping("/search")
	public JSONObject getAccountData(@RequestParam(required = false) String nickname) {
		JSONObject result = new JSONObject();
		
		if(nickname == null || nickname.equals("")) {
			result.put("error", "nickname is null");
			return result;
		}
		
		Account account = accountService.findAccountByNickname(nickname);
		result.put("result : ", account);
		return result;
	}
}
