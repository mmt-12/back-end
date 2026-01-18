package com.memento.server.config.filter;

import static com.memento.server.config.SecurityConstants.PUBLIC_PATHS;
import static com.memento.server.config.SecurityConstants.PUBLIC_PATH_PATTERNS;

import java.io.IOException;

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

/**
 * JWT 토큰 검증 필터
 * - 토큰이 유효하면 SecurityContext에 인증 정보 설정
 * - 토큰이 없거나 유효하지 않으면 SecurityContext를 설정하지 않음 (Spring Security가 처리)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberClaimValidator memberClaimValidator;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // OPTIONS 요청은 CORS preflight이므로 필터 건너뜀
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // 공개 경로는 필터 건너뜀
        boolean isPublicPath = PUBLIC_PATHS.stream().anyMatch(p -> pathMatcher.match(p, path));

        boolean isPublicPattern = PUBLIC_PATH_PATTERNS.stream().anyMatch(p -> pathMatcher.match(p, path));

        // 개발 도구 관련 경로
        boolean isDevToolPath = path.contains(".well-known") || path.contains("com.chrome.devtools.json");

        return isPublicPath || isPublicPattern || isDevToolPath;
    }

    @Override
    public void doFilterInternal(
        @NotNull HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull FilterChain chain
    ) throws IOException, ServletException {
        String token = resolveToken(request);

        // 토큰이 없거나 유효하지 않으면 SecurityContext를 설정하지 않고 통과
        // Spring Security가 인증되지 않은 요청으로 처리하여 AuthenticationEntryPoint 호출
        if (!StringUtils.hasText(token) || jwtTokenProvider.isNotValidateToken(token)) {
            SecurityContextHolder.clearContext();
            chain.doFilter(request, response);
            return;
        }

        MemberClaim memberClaim = jwtTokenProvider.extractMemberClaim(token);

        // MemberClaim 검증 실패 시에도 인증되지 않은 상태로 처리
        if (memberClaim.isMember() && !memberClaimValidator.isValid(memberClaim)) {
            SecurityContextHolder.clearContext();
            chain.doFilter(request, response);
            return;
        }

        // 인증 성공 - SecurityContext에 인증 정보 설정
        MemberPrincipal memberPrincipal = MemberPrincipal.from(memberClaim);
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
