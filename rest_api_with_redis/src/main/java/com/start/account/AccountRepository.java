package com.start.account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
 
	 Optional<Account> findByNickname(String nickname);
	 
	 Optional<Account> findByNicknameOrEmail(String nickname, String email);
}
