package com.start.redis;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.start.jwt.AccessToken;

public interface RedisLogoutTokenRepository extends CrudRepository<AccessToken, Long> {
	

	Optional<AccessToken> findByAccessToken(String accessToken);
	
	Optional<AccessToken> findByNickname(String nickname);
}
