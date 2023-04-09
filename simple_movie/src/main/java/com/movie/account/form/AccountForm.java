package com.movie.account.form;

import java.time.LocalDate;

import com.movie.account.domain.Account;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountForm {
	
	private Long id;
	private String nickname;
	private String password;
	private String email;
	private String role;
	private LocalDate joinDate;
	
	public AccountForm(Account account) {
		setEmail(account.getEmail());
		setNickname(account.getNickname());
		setJoinDate(account.getJoinDate());
		setRole(account.getRole());
	}
}
