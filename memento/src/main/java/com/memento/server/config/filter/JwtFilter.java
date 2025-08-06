package com.memento.server.config.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import com.memento.server.api.service.auth.MemberPrincipal;
import com.memento.server.api.service.auth.jwt.JwtTokenProvider;
import com.memento.server.api.service.auth.jwt.MemberClaim;

import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	private static final List<String> WHITELIST = List.of(
		"/favicon.ico",
		"/api/v1/sign-in",
		"/redirect",
		"/error",
		"/h2-console/**"
	);

	private boolean isWhitelisted(String path) {
		return WHITELIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
		throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		String token = resolveToken(request);
		String path = request.getRequestURI();

		if (isWhitelisted(path)) {
			chain.doFilter(request, response);
			return;
		}

		if (path.contains(".well-known") || path.contains("com.chrome.devtools.json")) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("{\"status\": \"ok\", \"message\": \"Chrome DevTools auto request ignored\"}");
			return;
		}

		if (!StringUtils.hasText(token) || !jwtTokenProvider.validateToken(token)) {
			throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
		}

		MemberClaim memberClaim = jwtTokenProvider.extractMemberClaim(token);
		MemberPrincipal memberPrincipal = new MemberPrincipal(memberClaim.memberId(), memberClaim.associateId(),
			memberClaim.communityId());
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			memberPrincipal, null, memberPrincipal.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);

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
