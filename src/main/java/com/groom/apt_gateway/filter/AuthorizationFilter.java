package com.groom.apt_gateway.filter;

import com.groom.apt_gateway.exception.GatewayErrorCode;
import com.groom.apt_gateway.exception.GatewayException;
import com.groom.apt_gateway.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationFilter implements GatewayFilter, Ordered {

	private final JwtUtil jwtUtil;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getPath().value();

		// Authorization 헤더 확인
		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (!StringUtils.hasText(authHeader)) {
			log.warn("Missing Authorization header for path: {}", path);
			throw new GatewayException(GatewayErrorCode.MISSING_TOKEN);
		}

		if (!authHeader.startsWith("Bearer ")) {
			log.warn("Invalid Authorization header format for path: {}", path);
			throw new GatewayException(GatewayErrorCode.MALFORMED_TOKEN);
		}

		String token = authHeader.substring(7);

		// 토큰 유효성 검증
		if (jwtUtil.isExpiredToken(token)) {
			log.warn("Expired token for path: {}", path);
			throw new GatewayException(GatewayErrorCode.EXPIRED_TOKEN);
		}

		if (!jwtUtil.validateToken(token)) {
			log.warn("Invalid token for path: {}", path);
			throw new GatewayException(GatewayErrorCode.INVALID_TOKEN);
		}

		// 사용자 정보 추출
		String userId = jwtUtil.getUserIdFromToken(token).toString();
		String role = jwtUtil.getRoleFromToken(token);

		log.debug("Authenticated user: {} with role: {} for path: {}", userId, role, path);

		// 헤더에 사용자 정보 추가 (각 서비스에서 사용)
		ServerHttpRequest modifiedRequest = request.mutate()
			.header("X-User-Id", userId)
			.header("X-User-Role", role)
			.build();

		return chain.filter(exchange.mutate().request(modifiedRequest).build());
	}

	@Override
	public int getOrder() {
		return -1;  // 가장 먼저 실행
	}
}
