package com.memento.server.config.filter;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import java.io.IOException;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.memento.server.api.service.auth.MemberPrincipal;
import com.memento.server.api.service.auth.jwt.JwtTokenProvider;
import com.memento.server.api.service.auth.jwt.MemberClaim;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberClaimValidator memberClaimValidator;
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> WHITELIST = List.of(
		"/favicon.ico",
		"/api/v1/sign-in",
		"/api/v1/auth/redirect",
		"/api/v1/auth/refresh",
		"/v1/sign-in",
		"/v1/auth/redirect",
		"/v1/auth/refresh",
		"/h2-console/**"
	);

	private boolean isWhitelisted(String path) {
		return WHITELIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return WHITELIST.stream().anyMatch(p -> pathMatcher.match(p, path))
			|| path.contains(".well-known")
			|| path.contains("com.chrome.devtools.json");
	}

	@Override
	public void doFilterInternal(
		@NotNull HttpServletRequest request,
		@NotNull HttpServletResponse response,
		@NotNull FilterChain chain
	) throws IOException, ServletException {
		// Ensure whitelist passes through regardless of token state
		String path = request.getRequestURI();
		if (isWhitelisted(path)) {
			chain.doFilter(request, response);
			return;
		}

		String token = resolveToken(request);

		if (!StringUtils.hasText(token) || !jwtTokenProvider.validateToken(token)) {
			SecurityContextHolder.clearContext();
			response.sendError(SC_UNAUTHORIZED, "토큰이 없거나 검증에 실패했습니다.");
			return;
		}

		MemberClaim memberClaim = jwtTokenProvider.extractMemberClaim(token);
		if (memberClaim.isMember() && !memberClaimValidator.isValid(memberClaim)) {
			SecurityContextHolder.clearContext();
			response.sendError(SC_UNAUTHORIZED, "MemberClaim 검증에 실패했습니다.");
			return;
		}

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
