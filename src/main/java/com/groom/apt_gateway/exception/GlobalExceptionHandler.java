package com.groom.apt_gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(-1)  // 가장 높은 우선순위
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

	private final ObjectMapper objectMapper;

	public GlobalExceptionHandler() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
	}

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		ServerHttpResponse response = exchange.getResponse();
		String path = exchange.getRequest().getPath().value();

		// 이미 응답이 시작된 경우
		if (response.isCommitted()) {
			return Mono.error(ex);
		}

		GatewayErrorResponse errorResponse;

		if (ex instanceof GatewayException gatewayEx) {
			// Gateway 커스텀 예외
			GatewayErrorCode errorCode = gatewayEx.getErrorCode();
			response.setStatusCode(errorCode.getHttpStatus());
			errorResponse = GatewayErrorResponse.of(errorCode, gatewayEx.getMessage(), path);
			log.warn("Gateway Exception: {} - {}", errorCode.getCode(), gatewayEx.getMessage());

		} else if (ex instanceof ResponseStatusException statusEx) {
			// Spring 기본 상태 예외
			response.setStatusCode(statusEx.getStatusCode());
			errorResponse = GatewayErrorResponse.builder()
				.code("G000")
				.message(statusEx.getReason())
				.status(statusEx.getStatusCode().value())
				.path(path)
				.timestamp(java.time.LocalDateTime.now())
				.build();
			log.warn("Response Status Exception: {}", statusEx.getMessage());

		} else {
			// 알 수 없는 예외
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			errorResponse = GatewayErrorResponse.of(GatewayErrorCode.INTERNAL_SERVER_ERROR, path);
			log.error("Unexpected Exception: ", ex);
		}

		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		try {
			byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
			DataBuffer buffer = response.bufferFactory().wrap(bytes);
			return response.writeWith(Mono.just(buffer));
		} catch (JsonProcessingException e) {
			log.error("Error writing response", e);
			return Mono.error(e);
		}
	}
}
