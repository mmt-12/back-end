package com.memento.server.domain.signup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class SignupPendingRepositoryTest {

	private SignupPendingRepository signupPendingRepository;

	@Mock
	private StringRedisTemplate redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@BeforeEach
	void setUp() {
		signupPendingRepository = new SignupPendingRepository(redisTemplate);
	}

	@Test
	@DisplayName("회원가입 대기 정보를 저장한다.")
	void save() {
		// given
		Long memberId = 1L;
		String fcmToken = "fcm-token-123";

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);

		// when
		signupPendingRepository.save(memberId, fcmToken);

		// then
		verify(valueOperations).set(eq("signup:pending:1"), eq(fcmToken), eq(7L), eq(TimeUnit.DAYS));
	}

	@Test
	@DisplayName("회원가입 대기 정보를 조회한다.")
	void findByMemberId() {
		// given
		Long memberId = 1L;
		String fcmToken = "fcm-token-123";

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get("signup:pending:1")).thenReturn(fcmToken);

		// when
		Optional<String> result = signupPendingRepository.findByMemberId(memberId);

		// then
		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(fcmToken);
	}

	@Test
	@DisplayName("존재하지 않는 memberId로 조회하면 빈 Optional을 반환한다.")
	void findByMemberId_notFound() {
		// given
		Long memberId = 9999L;

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get("signup:pending:9999")).thenReturn(null);

		// when
		Optional<String> result = signupPendingRepository.findByMemberId(memberId);

		// then
		assertThat(result).isEmpty();
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
