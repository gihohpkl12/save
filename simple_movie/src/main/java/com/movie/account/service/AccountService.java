package com.movie.account.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.movie.account.domain.Account;
import com.movie.account.form.AccountForm;
import com.movie.account.form.AccountJoinForm;
import com.movie.account.form.PasswordChangeForm;
import com.movie.account.repository.AccountRepository;
import com.movie.exception.account.AccountException;
import com.movie.like.repository.LikeRepository;
import com.movie.mail.EmailMessage;
import com.movie.mail.MailService;
import com.movie.review.repository.ReviewRepository;
import com.movie.security.role.RoleService;
import com.movie.security.role.domain.Role;
import com.movie.util.LoginUserUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private LikeRepository likeRepository;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<Account> getAllAccount() {
		return accountRepository.findAll();
	}
	
	public String getNickname(String email) {
		Optional<Account> account = accountRepository.findByEmail(email);
		if(!account.isPresent()) {
			throw new AccountException("해당 계정이 존재하지 않습니다");
		}
		
		return account.get().getNickname();
	}
	
	@Transactional
	public void checkPasswordToken(String nickname, String token) {
		if(mailService.checkTokenAndNickname(nickname, token)) {
			initPassword(nickname);
		} else {
			throw new AccountException("해당 토큰이 유효하지 않습니다");
		}
	}
	
	@Transactional
	private void initPassword(String nickname) {
		Account account = accountRepository.findByNickname(nickname);
		account.setPassword(passwordEncoder.encode("0000"));
	}
	
	public void createAndSendFindingPasswordToken(String nickname) {
		Account account = accountRepository.findByNickname(nickname);
		
		if(account == null) {
			throw new AccountException("해당 계정이 존재하지 않습니다");
		}
		
		String token = mailService.createToken();
		
		Context context = new Context();
		context.setVariable("token", token);
		context.setVariable("nickname", nickname);
		context.setVariable("message", "비빌번호 변경 토큰");
		
		String message = templateEngine.process("mail-form", context);
		
		EmailMessage email = new EmailMessage();
		email.setMessage(message);
		email.setSubject("비밀번호 찾기");
		email.setTo(account.getEmail());
		
		mailService.sendEmail(email, account.getNickname(), token);
	}
	
	public Account getAccount(String nickname) {
		Account account = accountRepository.findByNickname(nickname);
		if(account == null) {
			throw new AccountException("해당 계정이 존재하지 않습니다");
		}
		
		return account;
	}
	
	@Transactional
	public void changePassword(PasswordChangeForm passwordChangeFrom) {
		if(LoginUserUtil.isLogin()) {
			Account account = accountRepository.findByNickname(LoginUserUtil.getUserNickname());
			
			// 인코딩 안 한 비밀번호와 인코딩 된 비밀번호 순서로 입력.
			if(passwordEncoder.matches(passwordChangeFrom.getPassword(), account.getPassword())) {
				account.setPassword(passwordEncoder.encode(passwordChangeFrom.getNewPassword()));
			} else {
				throw new BadCredentialsException("기존 비밀번호를 확인해주세요.");
			}
		} else {
			throw new AuthenticationServiceException("로그인 후에 가능합니다.");
		}
	}
	
	public void checkBeforeAddAccount(AccountJoinForm newAccount) {
		Account account = accountRepository.findByNicknameOrEmail(newAccount.getNickname(), newAccount.getEmail());
		
		if(account != null) {
			throw new AuthenticationServiceException("이미 존재하는 이메일, 아이디 입니다.");
		}
		
		addAccount(newAccount);
	}

	private void addAccount(AccountJoinForm newAccount) {
		Account account = new Account();
		account.setEmail(newAccount.getEmail());
		account.setNickname(newAccount.getNickname());
		account.setPassword(passwordEncoder.encode(newAccount.getPassword()));
		account.setJoinDate(LocalDate.now());
		account.setRole("ROLE_USER");
		
		accountRepository.save(account);
	}

	@Transactional
	@PreAuthorize("hasRole('ROLE_USER')")
	public void checkBeforeEdit(AccountForm accountForm) {
		Optional<Account> account = accountRepository.findById(accountForm.getId());
		
		if(!account.isPresent()) {
			throw new AccountException("해당 아이디가 없습니다");
		}
		
		if(!accountForm.getNickname().equals(account.get().getNickname())) {
			Optional<Account> accountForNicknameCheck = accountRepository.findByNicknameAndIdNot(accountForm.getNickname(), account.get().getId());
			
			if(accountForNicknameCheck.isPresent()) {
				throw new AccountException("해당 닉네임이 이미 존재합니다");
			}
		}
		
		if(!accountForm.getEmail().equals(account.get().getEmail())) {
			Optional<Account> accountForEmailCheck = accountRepository.findByEmailAndIdNot(accountForm.getEmail(), account.get().getId());
			
			if(accountForEmailCheck.isPresent()) {
				throw new AccountException("해당 이메일이 이미 존재합니다");
			}
		}
		
		if(!accountForm.getRole().equals(account.get().getRole())) {
			if(!LoginUserUtil.getAccount().getRole().equals("ROLE_ADMIN")) {
				throw new AccountException("권한은 관리자만 변경할 수 있습니다");
			}
			
			Optional<Role> role = roleService.getRoleByRoleName(accountForm.getRole());
			
			if(!role.isPresent()) {
				throw new AccountException("해당 권한이 없습니다");
			}
		}
		
		edit(account.get(), accountForm);
	}

	private void edit(Account account, AccountForm accountForm) {
		account.setEmail(accountForm.getEmail());
		account.setNickname(accountForm.getNickname());
		account.setRole(accountForm.getRole());
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional
	public void checkBeforeDeleteByAdmin(Long id) {
		Optional<Account> account = accountRepository.findById(id);
		
		if(!account.isPresent()) {
			throw new AccountException("해당 계정이 존재하지 않습니다");
		}
		
		delete(account.get());
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional
	public void checkBeforeDeleteByUser(String nickname, String password) {
		if(!LoginUserUtil.isLogin()) {
			throw new AccountException("로그인 후에 탈퇴할 수 있습니다");
		}
		
		if(LoginUserUtil.getUserNickname().equals(nickname)) {
			if(passwordEncoder.matches(password, LoginUserUtil.getAccount().getPassword())) {
				Account account = accountRepository.findByNickname(nickname);
				
				if(account == null) {
					throw new AccountException("해당 사용자가 존재하지 않습니다");
				}
				
				delete(account);
			} else {
				throw new AccountException("비밀번호가 일치하지 않습니다");
			}
		} else {
			throw new AccountException("닉네임이 불일치합니다");
		}
	}

	@Transactional
	private void delete(Account account) {
		likeRepository.deleteAllByNickname(account.getNickname());
		reviewRepository.deleteAllByNickname(account.getNickname());
		accountRepository.delete(account);
	}
}
