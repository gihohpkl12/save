package com.start.jwt;

public class JwtTokenContent {

	private static final long ACCESS_TOKEN_LIMIT_TIME = 60 * 60 * 1000l;
	private static final long REFRESH_TOKEN_LIMIT_TIME = 240 * 60 * 1000l;

	public static long getAccessTokenTime() {
		return ACCESS_TOKEN_LIMIT_TIME;
	}

	public static long getRefreshTokenTime() {
		return REFRESH_TOKEN_LIMIT_TIME;
	}
}
