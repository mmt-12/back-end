package com.memento.server.domain.signup;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SignupPendingRepository {

	private static final String KEY_PREFIX = "signup:pending:";
	private static final String FCM_TOKEN_FIELD = "fcmToken";
	private static final String SECURITY_TOKEN_FIELD = "token";
	private static final long TTL_DAYS = 7;

	private final StringRedisTemplate redisTemplate;

	public String save(Long memberId, String fcmToken) {
		String key = KEY_PREFIX + memberId;
		String securityToken = UUID.randomUUID().toString();

		redisTemplate.opsForHash().put(key, FCM_TOKEN_FIELD, fcmToken);
		redisTemplate.opsForHash().put(key, SECURITY_TOKEN_FIELD, securityToken);
		redisTemplate.expire(key, TTL_DAYS, TimeUnit.DAYS);

		return securityToken;
	}

	public Optional<String> findFcmTokenByMemberId(Long memberId) {
		String key = KEY_PREFIX + memberId;
		Object fcmToken = redisTemplate.opsForHash().get(key, FCM_TOKEN_FIELD);
		return Optional.ofNullable((String) fcmToken);
	}

	public Optional<String> findSecurityTokenByMemberId(Long memberId) {
		String key = KEY_PREFIX + memberId;
		Object token = redisTemplate.opsForHash().get(key, SECURITY_TOKEN_FIELD);
		return Optional.ofNullable((String) token);
	}

	public boolean verifyToken(Long memberId, String token) {
		Optional<String> storedToken = findSecurityTokenByMemberId(memberId);
		return storedToken.isPresent() && storedToken.get().equals(token);
	}

	public void deleteByMemberId(Long memberId) {
		String key = KEY_PREFIX + memberId;
		redisTemplate.delete(key);
	}
}
