package com.start.security.authentication;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.start.account.Account;

import lombok.Getter;

/*
 * User 클래스는 UserDeatails 클래스를 상속받음.
 * 
 * implements UserDetails 해도 되는데
 * 그러면 구현하지 않을 메소드가 너무 많아져서 그냥 User를 extends함.
 */

@Getter
public class CustomUserDetail extends User {
	
	private Account account;
	
	public CustomUserDetail(Account account, Collection<? extends GrantedAuthority> authorities) {
		super(account.getNickname(), account.getPassword(), authorities);
		this.account = account;
	}
}
