package com.memento.server.domain.signup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

@ExtendWith(MockitoExtension.class)
class SignupPendingRepositoryTest {

	private SignupPendingRepository signupPendingRepository;

	@Mock
	private StringRedisTemplate redisTemplate;

	@Mock
	private HashOperations<String, Object, Object> hashOperations;

	@BeforeEach
	void setUp() {
		signupPendingRepository = new SignupPendingRepository(redisTemplate);
	}

	@Test
	@DisplayName("회원가입 대기 정보를 저장하고 보안 토큰을 반환한다.")
	void save() {
		// given
		Long memberId = 1L;
		String fcmToken = "fcm-token-123";

		when(redisTemplate.opsForHash()).thenReturn(hashOperations);

		// when
		String securityToken = signupPendingRepository.save(memberId, fcmToken);

		// then
		assertThat(securityToken).isNotNull();
		assertThat(securityToken).isNotEmpty();
		verify(hashOperations).put(eq("signup:pending:1"), eq("fcmToken"), eq(fcmToken));
		verify(hashOperations).put(eq("signup:pending:1"), eq("token"), eq(securityToken));
		verify(redisTemplate).expire(eq("signup:pending:1"), eq(7L), eq(TimeUnit.DAYS));
	}

	@Test
	@DisplayName("FCM 토큰을 조회한다.")
	void findFcmTokenByMemberId() {
		// given
		Long memberId = 1L;
		String fcmToken = "fcm-token-123";

		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		when(hashOperations.get("signup:pending:1", "fcmToken")).thenReturn(fcmToken);

		// when
		Optional<String> result = signupPendingRepository.findFcmTokenByMemberId(memberId);

		// then
		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(fcmToken);
	}

	@Test
	@DisplayName("보안 토큰을 조회한다.")
	void findSecurityTokenByMemberId() {
		// given
		Long memberId = 1L;
		String securityToken = "test-security-token";

		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		when(hashOperations.get("signup:pending:1", "token")).thenReturn(securityToken);

		// when
		Optional<String> result = signupPendingRepository.findSecurityTokenByMemberId(memberId);

		// then
		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(securityToken);
	}

	@Test
	@DisplayName("존재하지 않는 memberId로 조회하면 빈 Optional을 반환한다.")
	void findFcmTokenByMemberId_notFound() {
		// given
		Long memberId = 9999L;

		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		when(hashOperations.get("signup:pending:9999", "fcmToken")).thenReturn(null);

		// when
		Optional<String> result = signupPendingRepository.findFcmTokenByMemberId(memberId);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("토큰 검증에 성공한다.")
	void verifyToken_success() {
		// given
		Long memberId = 1L;
		String token = "valid-token";

		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		when(hashOperations.get("signup:pending:1", "token")).thenReturn(token);

		// when
		boolean result = signupPendingRepository.verifyToken(memberId, token);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("잘못된 토큰으로 검증에 실패한다.")
	void verifyToken_fail() {
		// given
		Long memberId = 1L;
		String storedToken = "stored-token";
		String wrongToken = "wrong-token";

		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		when(hashOperations.get("signup:pending:1", "token")).thenReturn(storedToken);

		// when
		boolean result = signupPendingRepository.verifyToken(memberId, wrongToken);

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("회원가입 대기 정보를 삭제한다.")
	void delete() {
		// given
		Long memberId = 2L;

		// when
		signupPendingRepository.deleteByMemberId(memberId);

		// then
		verify(redisTemplate).delete("signup:pending:2");
	}
}
