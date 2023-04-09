package com.start.jwt;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import jakarta.persistence.Id;
import lombok.Data;

@RedisHash("refreshToken")
@Data
public class RefreshToken implements Serializable {

	@Id
	private Long id;

	@Indexed
	private String refreshToken;
	
	@Indexed
	private String nickname;

	@TimeToLive
	private Long expiration;
}
