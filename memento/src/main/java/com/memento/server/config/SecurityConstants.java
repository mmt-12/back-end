package com.memento.server.config;

import java.util.List;

/**
 * Security 관련 공통 상수 정의
 * Single Source of Truth - 모든 Security 설정에서 이 상수를 참조
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // 인스턴스화 방지
    }

    /**
     * 인증이 필요 없는 공개 API 경로 목록
     */
    public static final List<String> PUBLIC_PATHS = List.of(
        // Health check
        "/api/v1/health",
        "/v1/health",

        // OAuth & Auth
        "/api/v1/sign-in",
        "/api/v1/auth/redirect",
        "/api/v1/auth/refresh",
        "/v1/sign-in",
        "/v1/auth/redirect",
        "/v1/auth/refresh",

        // 일반 회원가입 관련
        "/api/v1/members/signup/normal",
        "/api/v1/members/signin",
        "/api/v1/members/check-email",
        "/api/v1/members/signup/page",
        "/api/v1/members/signup/result",
        "/v1/members/signup/normal",
        "/v1/members/signin",
        "/v1/members/check-email",
        "/v1/members/signup/page",
        "/v1/members/signup/result",

        // Static resources
        "/favicon.ico",
        "/error"
    );

    /**
     * Ant 패턴을 사용하는 공개 경로 (h2-console 등)
     */
    public static final List<String> PUBLIC_PATH_PATTERNS = List.of(
        "/h2-console/**"
    );

    /**
     * 모든 공개 경로를 String 배열로 반환 (SecurityConfig에서 사용)
     */
    public static String[] getPublicPathsArray() {
        int totalSize = PUBLIC_PATHS.size() + PUBLIC_PATH_PATTERNS.size();
        String[] result = new String[totalSize];
        int index = 0;
        for (String path : PUBLIC_PATHS) {
            result[index++] = path;
        }
        for (String pattern : PUBLIC_PATH_PATTERNS) {
            result[index++] = pattern;
        }
        return result;
    }
}
