package com.groom.apt_gateway.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatewayErrorResponse {

	private final String code;
	private final String message;
	private final int status;
	private final String path;
	private final LocalDateTime timestamp;

	public static GatewayErrorResponse of(GatewayErrorCode errorCode, String path) {
		return GatewayErrorResponse.builder()
			.code(errorCode.getCode())
			.message(errorCode.getMessage())
			.status(errorCode.getHttpStatus().value())
			.path(path)
			.timestamp(LocalDateTime.now())
			.build();
	}

	public static GatewayErrorResponse of(GatewayErrorCode errorCode, String message, String path) {
		return GatewayErrorResponse.builder()
			.code(errorCode.getCode())
			.message(message)
			.status(errorCode.getHttpStatus().value())
			.path(path)
			.timestamp(LocalDateTime.now())
			.build();
	}
}
