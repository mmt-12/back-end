package com.memento.server.domain.signup;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SignupPendingRepository {

	private static final String KEY_PREFIX = "signup:pending:";
	private static final long TTL_DAYS = 7;

	private final StringRedisTemplate redisTemplate;

	public void save(Long memberId, String fcmToken) {
		String key = KEY_PREFIX + memberId;
		redisTemplate.opsForValue().set(key, fcmToken, TTL_DAYS, TimeUnit.DAYS);
	}

	public Optional<String> findByMemberId(Long memberId) {
		String key = KEY_PREFIX + memberId;
		String fcmToken = redisTemplate.opsForValue().get(key);
		return Optional.ofNullable(fcmToken);
	}

	public void deleteByMemberId(Long memberId) {
		String key = KEY_PREFIX + memberId;
		redisTemplate.delete(key);
	}
}
