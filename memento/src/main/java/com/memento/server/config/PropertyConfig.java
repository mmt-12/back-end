package com.memento.server.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.memento.server.client.oauth.KakaoProperties;

@Configuration
@EnableConfigurationProperties(KakaoProperties.class)
public class PropertyConfig {
}
