package com.memento.server.spring.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.memento.server.config.filter.JwtFilter;
import com.memento.server.api.service.auth.jwt.JwtTokenProvider;

@TestConfiguration
public class TestSecurityConfig {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/api/v1/sign-in", "/redirect").permitAll()
				.requestMatchers("/error").permitAll()
				.anyRequest().authenticated())
			.addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
			.cors(Customizer.withDefaults())
			.build();
	}
}
