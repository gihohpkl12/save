package com.start.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.start.account.Account;

public class CustomAuthenticationProvider implements AuthenticationProvider{

	@Autowired
	CustomAccountDetailsService customAccountDetailsService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		 CustomUserDetail account = (CustomUserDetail)customAccountDetailsService.loadUserByUsername(authentication.getName());
		
		if(account == null) {
			throw new BadCredentialsException("account null");
		}
		
		if(!passwordEncoder.matches((String)authentication.getCredentials(), account.getPassword())) {
			throw new BadCredentialsException("password error");
		}
		
		return new UsernamePasswordAuthenticationToken(account.getAccount(), null, account.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return false;
	}

}
