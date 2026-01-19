package com.groom.apt_gateway.filter;

import com.groom.apt_gateway.jwt.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationFilterFactory extends AbstractGatewayFilterFactory<AuthorizationFilterFactory.Config> {

	private final JwtUtil jwtUtil;

	public AuthorizationFilterFactory(JwtUtil jwtUtil) {
		super(Config.class);      // 부모 초기화 (Gateway용)
		this.jwtUtil = jwtUtil;   // 의존성 주입 (JWT 검증용)
	}

	@Override
	public GatewayFilter apply(Config config) {
		return new AuthorizationFilter(jwtUtil);
	}

	@Override
	public String name() {
		return "AuthorizationFilter";
	}

	public static class Config {
		// 필요시 설정 추가
	}
}
