package com.groom.apt_gateway.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GatewayErrorCode {

	// Auth (기존 ErrorCode 유지)
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN", "만료된 토큰입니다."),
	MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "MISSING_TOKEN", "토큰이 없습니다."),
	MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "MALFORMED_TOKEN", "잘못된 토큰 형식입니다."),

	// Role
	ROLE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "ROLE_NOT_ALLOWED", "허용되지 않은 역할입니다."),

	// Gateway
	SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", "서비스를 사용할 수 없습니다."),
	GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "GATEWAY_TIMEOUT", "서비스 응답 시간 초과"),

	// Common
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
