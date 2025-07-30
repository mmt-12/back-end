package com.memento.server.config;

import java.io.IOException;

import org.springframework.util.StringUtils;

import com.memento.server.service.auth.jwt.JwtTokenProvider;

import jakarta.security.auth.message.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtFilter extends GenericFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {
		String token = resolveToken((HttpServletRequest)request);
		String path = ((HttpServletRequest)request).getRequestURI();

		if (path.equals("/api/v1/sign-in") || path.equals("/redirect") || path.startsWith("/h2-console")) {
			chain.doFilter(request, response);
			return;
		}

		if (!StringUtils.hasText(token) || !jwtTokenProvider.validateToken(token)) {
			throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
		}

		chain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
