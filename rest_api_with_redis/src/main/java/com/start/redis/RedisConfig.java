package com.start.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/*
 * 이건 redis에서 session을 관리할 때.
 * Lettuce를 사용한 ConnectionFactory Bean 등록.
*/

//maxInactiveIntervalInSeconds = 10 -> 만료시간 10초
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 10)

// EnableRedisRepositories을 최상위에 입력해야 함
@EnableRedisRepositories
@Configuration
public class RedisConfig {

	@Value("${spring.redis.host}")
	private String redisHost;

	@Value("${spring.redis.port}")
	private int redisPort;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisHost, redisPort);
	}
}
