package com.start.redis;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.start.jwt.AccessToken;
import com.start.jwt.RefreshToken;

public interface RedisRefreshTokenRepository extends CrudRepository<RefreshToken, Long>{

	Optional<RefreshToken> findByRefreshToken(String refreshToken);
	
	Optional<RefreshToken> findByNickname(String nickname);
	
	void deleteByNickname(String nickname);
}
