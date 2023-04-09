package com.movie.security.authentication;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.movie.account.domain.Account;


public class CustomUserDetails extends User {

	private Account account;

	public CustomUserDetails(Account account, Collection<? extends GrantedAuthority> authorities) {
		super(account.getNickname(), account.getPassword(), authorities);
		this.account = account;
	}
	
	public Account getAccount() {
		return this.account;
	}
}
