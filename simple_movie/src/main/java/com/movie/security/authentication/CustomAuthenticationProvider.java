package com.movie.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.movie.account.domain.Account;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private UserDetailsService customAccountDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String nickname = authentication.getName();
		String password = (String)authentication.getCredentials();
		
		// 인코딩 안 한 비밀번호와 인코딩 된 비밀번호 순서로 입력.
		CustomUserDetails user = (CustomUserDetails)customAccountDetailsService.loadUserByUsername(nickname);
		if(!passwordEncoder.matches(password, user.getPassword())) {
			throw new BadCredentialsException("password error");
		}
		
		Account account = user.getAccount();
		// UsernamePasswordAuthenticationToken는 올라가다보면 Authentication를 상속받는 Authentication용 토큰임.
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(account, null, user.getAuthorities());
		
		return authenticationToken;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		System.out.println("?? supprot call  "+UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
