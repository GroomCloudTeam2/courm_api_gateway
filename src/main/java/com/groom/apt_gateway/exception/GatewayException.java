package com.groom.apt_gateway.exception;

import lombok.Getter;

@Getter
public class GatewayException extends RuntimeException {

	private final GatewayErrorCode errorCode;

	public GatewayException(GatewayErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public GatewayException(GatewayErrorCode errorCode, String customMessage) {
		super(customMessage);
		this.errorCode = errorCode;
	}
}
