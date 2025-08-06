package com.memento.server.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.memento.server.client.oauth.KakaoClientProperties;
import com.memento.server.api.service.auth.jwt.JwtProperties;

@Configuration
@EnableConfigurationProperties({
	KakaoClientProperties.class,
	JwtProperties.class})
public class PropertyConfig {
}
