package com.start.jwt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.security.auth.login.AccountException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import com.start.redis.RedisLogoutTokenRepository;
import com.start.redis.RedisRefreshTokenRepository;
import com.start.security.authentication.CustomAccountDetailsService;
import com.start.security.authentication.CustomUserDetail;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenService {
	
	@Value("${jwt.secret.key}")
	private String secretKey;
	
	@Autowired
	RedisRefreshTokenRepository redisRefreshTokenRepository;
	
	@Autowired
	RedisLogoutTokenRepository redisLogoutTokenRepository;
	
	@Autowired
	CustomAccountDetailsService customAccountDetailsService;

	private String createJwtToken(String nickname, String role, long limitTime) {
		Date startTime = new Date();
		Date endTime = new Date(startTime.getTime() + limitTime);

		Claims claims = Jwts.claims().setSubject("access_token").setIssuer("jwt_test_Issuer").setIssuedAt(startTime).setExpiration(endTime);
		claims.put("nickname", nickname);
		claims.put("role", role);

		return Jwts.builder().setHeaderParam("type", "JWT").setClaims(claims).signWith(SignatureAlgorithm.HS256, secretKey).compact();
	}

	public String createRefreshToken(String nickname, String role) {
		return createJwtToken(nickname, role, JwtTokenContent.getRefreshTokenTime());
	}

	public String createAccessToken(String nickname, String role) {
		return createJwtToken(nickname, role, JwtTokenContent.getAccessTokenTime());
	}

	public String extractTokenFromRequest(HttpServletRequest request) {
		return request.getHeader("JWT");
	}
	
	public String extractRefreshTokenFromRequest(HttpServletRequest request) {
		return request.getHeader("JWT-REFRESH");
	}

	public Claims extractClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
	}

	public String getNickname(String token) {
		Claims claims = extractClaimsFromToken(token);
		
		if (claims != null) {
			return claims.get("nickname", String.class);
		}

		return "";
	}
	
	public String getRole(String token) {
		Claims claims = extractClaimsFromToken(token);
		
		if (claims != null) {
			return claims.get("role", String.class);
		}

		return "";
	}

	public Boolean isTokenExpired(String token) {
		
		try {
			Claims claims = extractClaimsFromToken(token);
			Date date = claims.getExpiration();
			if (date.before(new Date())) {
				return true;
			}
		} catch (ExpiredJwtException e) {
			return true;
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	public boolean validateRefreshTokenProcess(String refreshToken) {
		Optional<RefreshToken> token = redisRefreshTokenRepository.findByRefreshToken(refreshToken);
		
		if(!token.isPresent()) {
			return false;
		}
		
		return validateTokenProcess(refreshToken);
	}

	public boolean validateTokenProcess(String token) {
		String nickname = getNickname(token);
		
		if (nickname.equals("")) {
			return false;
		}
		
		String role = getRole(token);
		System.out.println(role);
		if(role.equals("")) {
			return false;
		}

		CustomUserDetail account = (CustomUserDetail)customAccountDetailsService.loadUserByUsername(nickname);
		if (account == null) {
			return false;
		}
		
		if(!account.getAccount().getNickname().equals(nickname) || !account.getAccount().getRole().equals(role)) {
			return false;
		}

		return true;
	}
	
	public Authentication createAuthentication(String token) {
		List<GrantedAuthority> role = new ArrayList<>();
		role.add(new SimpleGrantedAuthority(getRole(token)));
		
		return new UsernamePasswordAuthenticationToken(getNickname(token), null, role);
	}
	
	public boolean isLogout(String token) {
		Optional<AccessToken> logoutToken = redisLogoutTokenRepository.findByAccessToken(token);
		
		if(logoutToken.isPresent()) {
			return true;
		}
		
		return false;
	}
	
	private long getRemainAccessTokenTime(String token) {
		Claims claims = extractClaimsFromToken(token);
		Date date = claims.getIssuedAt();
		
		return date.getTime() - System.currentTimeMillis();
		
	}

	public void logout(String accessToken, String refreshToken, String nickname) throws AccountException {
		try {
			if(getNickname(accessToken).equals(nickname)) {
				if(!isTokenExpired(accessToken)) {
					if(validateTokenProcess(accessToken)) {
						AccessToken logoutToken = new AccessToken();
						logoutToken.setAccessToken(accessToken);
						logoutToken.setExpiration(getRemainAccessTokenTime(accessToken));
						logoutToken.setNickname(nickname);
						redisLogoutTokenRepository.save(logoutToken);
					} else {
						throw new AccountException("유효하지 않은 토큰입니다");
					}
				}
			} else {
				throw new AccountException("로그아웃 하려는 토큰과 로인한 계정의 닉네임이 서로 다릅니다");
			}
		} finally {
			Optional<RefreshToken> refreshTokenInRedis = redisRefreshTokenRepository.findByRefreshToken(refreshToken);
			
			if(refreshTokenInRedis.isPresent()) {
				redisRefreshTokenRepository.delete(refreshTokenInRedis.get());
			}
		}
	}
}
