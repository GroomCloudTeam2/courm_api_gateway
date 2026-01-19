package com.groom.apt_gateway.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RoleAuthorizationFilterFactory extends AbstractGatewayFilterFactory<RoleAuthorizationFilterFactory.Config> {

	public RoleAuthorizationFilterFactory() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		List<String> roles = Arrays.asList(config.getRoles().split(","));
		return new RoleAuthorizationFilter(roles);
	}

	@Override
	public String name() {
		return "RoleAuthorizationFilter";
	}

	@Getter
	@Setter
	public static class Config {
		private String roles;  // "OWNER" 또는 "MANAGER,MASTER"
	}
}

