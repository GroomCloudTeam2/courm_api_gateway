package com.groom.apt_gateway.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {

	@Value("${jwt.secret:defaultSecretKeyForDevelopmentPurposeOnly12345}")
	private String secretKey;

	private SecretKey key;

	@PostConstruct
	public void init() {
		byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes());
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public UUID getUserIdFromToken(String token) {
		return UUID.fromString(parseClaims(token).getSubject());
	}

	public String getRoleFromToken(String token) {
		return parseClaims(token).get("role", String.class);
	}

	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (ExpiredJwtException e) {
			log.warn("Expired JWT token");
			return false;
		} catch (JwtException e) {
			log.warn("Invalid JWT token: {}", e.getMessage());
			return false;
		}
	}

	public boolean isExpiredToken(String token) {
		try {
			parseClaims(token);
			return false;
		} catch (ExpiredJwtException e) {
			return true;
		} catch (JwtException e) {
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}
}
