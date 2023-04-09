package com.movie.mail;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MailTokenRepository extends JpaRepository<MailToken, Long> {
	
	Optional<MailToken> findByNicknameAndEmailToken(String nickname, String emailToken);

	void deleteByNickname(String nickname);

}
