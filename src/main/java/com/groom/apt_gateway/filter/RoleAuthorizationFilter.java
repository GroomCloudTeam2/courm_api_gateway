package com.groom.apt_gateway.filter;

import com.groom.apt_gateway.exception.GatewayErrorCode;
import com.groom.apt_gateway.exception.GatewayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class RoleAuthorizationFilter implements GatewayFilter, Ordered {

	private final List<String> allowedRoles;

	public RoleAuthorizationFilter(List<String> allowedRoles) {
		this.allowedRoles = allowedRoles;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getPath().value();

		// AuthorizationFilter에서 추가한 X-User-Role 헤더 확인
		String role = request.getHeaders().getFirst("X-User-Role");

		if (role == null) {
			log.warn("Missing X-User-Role header for path: {}", path);
			throw new GatewayException(GatewayErrorCode.UNAUTHORIZED);
		}

		if (!allowedRoles.contains(role)) {
			log.warn("Access denied. Role: {}, Required: {}, Path: {}", role, allowedRoles, path);
			throw new GatewayException(GatewayErrorCode.ROLE_NOT_ALLOWED,
				String.format("접근 권한이 없습니다. 필요 역할: %s", allowedRoles));
		}

		log.debug("Role authorization passed. Role: {}, Path: {}", role, path);

		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return 0;  // AuthorizationFilter(-1) 다음에 실행
	}
}

