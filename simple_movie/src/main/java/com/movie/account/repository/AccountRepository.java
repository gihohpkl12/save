package com.movie.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.movie.account.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

	@Transactional
	Account findByNickname(String nickname);
	Account findByNicknameOrEmail(String nickname, String email);
	Optional<Account> findByEmail(String email);
	Optional<Account> findByNicknameAndIdNot(String nickname, Long id);
	Optional<Account> findByEmailAndIdNot(String email, Long id);

}
