package com.start.account;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.start.account.form.JoinForm;
import com.start.account.form.LoginForm;
import com.start.exception.AccountException;
import com.start.jwt.AccessToken;
import com.start.jwt.JwtTokenContent;
import com.start.jwt.JwtTokenService;
import com.start.jwt.RefreshToken;
import com.start.redis.RedisLogoutTokenRepository;
import com.start.redis.RedisRefreshTokenRepository;
import com.start.security.authentication.CustomAccountDetailsService;
import com.start.security.authentication.CustomUserDetail;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AccountService {

	@Autowired
	private CustomAccountDetailsService customAccountDetailsService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RedisLogoutTokenRepository redisLogoutTokenRepository;

	@Autowired
	private RedisRefreshTokenRepository redisRefreshTokenRepository;

	@Autowired
	private JwtTokenService jwtTokenService;

	public void login(LoginForm loginForm, HttpServletResponse response) throws AccountException {

		CustomUserDetail account = (CustomUserDetail) customAccountDetailsService.loadUserByUsername(loginForm.getNickname());

		if (account == null) {
			throw new AccountException("해당 계정이 존재하지 않습니다");
		}

		if (passwordEncoder.matches(loginForm.getPassword(), account.getAccount().getPassword())) {
			String accessToken = jwtTokenService.createAccessToken(account.getAccount().getNickname(), account.getAccount().getRole());
			String refreshToken = jwtTokenService.createRefreshToken(account.getAccount().getNickname(), account.getAccount().getRole());

			Optional<AccessToken> logoutToken = redisLogoutTokenRepository.findByNickname(account.getAccount().getNickname());
			Optional<RefreshToken> oldRefreshToken = redisRefreshTokenRepository.findByNickname(account.getAccount().getNickname());

			if (logoutToken.isPresent()) {
				redisLogoutTokenRepository.delete(logoutToken.get());
			}

			if (oldRefreshToken.isPresent()) {
				redisRefreshTokenRepository.delete(oldRefreshToken.get());
			}

			RefreshToken save = new RefreshToken();
			save.setNickname(account.getAccount().getNickname());
			save.setExpiration(JwtTokenContent.getRefreshTokenTime());
			save.setRefreshToken(refreshToken);
			redisRefreshTokenRepository.save(save);

			response.setHeader("JWT", accessToken);
			response.setHeader("JWT-REFRESH", refreshToken);
		} else {
			throw new AccountException("해당 계정이 존재하지 않습니다");
		}

	}

	public void join(JoinForm joinForm) throws AccountException {
		if (!checkJoinForm(joinForm)) {
			throw new AccountException("닉네임, 이메일을 확인해주시기 바랍니다");
		}

		accountRepository.save(new Account(joinForm, "ROLE_USER"));
	}

	public boolean checkJoinForm(JoinForm joinForm) {
		Optional<Account> account = accountRepository.findByNicknameOrEmail(joinForm.getNickname(), joinForm.getEmail());

		if (account.isPresent()) {
			return false;
		}

		return true;
	}

	public Account findAccountByNickname(String nickname) {
		Optional<Account> account = accountRepository.findByNickname(nickname);

		if (!account.isPresent()) {
			return null;
		}

		return account.get();
	}
}
