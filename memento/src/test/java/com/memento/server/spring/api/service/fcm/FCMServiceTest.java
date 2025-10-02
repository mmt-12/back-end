package com.memento.server.spring.api.service.fcm;

import static com.memento.server.domain.notification.NotificationType.ACHIEVE;
import static com.memento.server.domain.notification.NotificationType.REACTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.memento.server.api.service.fcm.FCMService;
import com.memento.server.api.service.fcm.dto.request.BasicData;
import com.memento.server.api.service.fcm.dto.request.FCMRequest;
import com.memento.server.api.service.fcm.dto.request.ReactionData;
import com.memento.server.api.service.fcm.dto.request.ReceiverInfo;
import com.memento.server.client.fcm.FCMSender;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.fcm.FCMToken;
import com.memento.server.domain.fcm.FCMTokenRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

public class FCMServiceTest extends IntegrationsTestSupport {

	@Autowired
	private FCMService fcmService;

	@MockitoBean
	private FCMSender fcmSender;

	@Autowired
	private FCMTokenRepository fcmTokenRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@AfterEach
	public void tearDown() {
		fcmTokenRepository.deleteAllInBatch();
		associateRepository.deleteAllInBatch();
		communityRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("단일 수신자에게 FCM 알림을 전송한다")
	void sendToSingleAssociate() throws FirebaseMessagingException {
		// given
		FCMTestFixtures fixtures = createSingleReceiverFixtures();
		FCMRequest request = FCMRequest.of(
			ACHIEVE.getTitle(),
			List.of(ReceiverInfo.of(fixtures.receiver.getId(), "새로운 업적을 달성했어요")),
			BasicData.of(ACHIEVE)
		);

		// when
		fcmService.sendToAssociates(request);

		// then
		verify(fcmSender, times(1)).send(
			eq(fixtures.fcmToken.getToken()),
			eq(ACHIEVE.getTitle()),
			eq("새로운 업적을 달성했어요"),
			anyMap()
		);
	}

	@Test
	@DisplayName("다중 수신자에게 FCM 알림을 전송한다")
	void sendToMultipleAssociates() throws FirebaseMessagingException {
		// given
		FCMTestFixtures fixtures = createMultipleReceiverFixtures();
		FCMRequest request = FCMRequest.of(
			REACTION.getTitle(),
			List.of(
				ReceiverInfo.of(fixtures.receiver1.getId(), "포스트에 반응을 남겼어요"),
				ReceiverInfo.of(fixtures.receiver2.getId(), "포스트에 반응을 남겼어요")
			),
			ReactionData.of(1L, 1L)
		);

		// when
		fcmService.sendToAssociates(request);

		// then
		verify(fcmSender, times(2)).send(anyString(), anyString(), anyString(), anyMap());
		verify(fcmSender).send(
			eq(fixtures.fcmToken1.getToken()),
			eq(REACTION.getTitle()),
			eq("포스트에 반응을 남겼어요"),
			anyMap()
		);
		verify(fcmSender).send(
			eq(fixtures.fcmToken2.getToken()),
			eq(REACTION.getTitle()),
			eq("포스트에 반응을 남겼어요"),
			anyMap()
		);
	}

	@Test
	@DisplayName("FCM 토큰이 없는 수신자는 알림을 받지 않는다")
	void skipAssociateWithoutFCMToken() throws FirebaseMessagingException {
		// given
		FCMTestFixtures fixtures = createReceiverWithoutToken();
		FCMRequest request = FCMRequest.of(
			ACHIEVE.getTitle(),
			List.of(ReceiverInfo.of(fixtures.receiver.getId(), "새로운 업적을 달성했어요")),
			BasicData.of(ACHIEVE)
		);

		// when
		fcmService.sendToAssociates(request);

		// then
		verify(fcmSender, never()).send(anyString(), anyString(), anyString(), anyMap());
	}

	@Test
	@DisplayName("유효하지 않은 FCM 토큰은 삭제한다 - UNREGISTERED")
	void deleteInvalidTokenWhenUnregistered() throws FirebaseMessagingException {
		// given
		FCMTestFixtures fixtures = createSingleReceiverFixtures();
		FCMRequest request = FCMRequest.of(
			ACHIEVE.getTitle(),
			List.of(ReceiverInfo.of(fixtures.receiver.getId(), "새로운 업적을 달성했어요")),
			BasicData.of(ACHIEVE)
		);

		FirebaseMessagingException exception = createFirebaseException(MessagingErrorCode.UNREGISTERED);
		doThrow(exception).when(fcmSender).send(anyString(), anyString(), anyString(), anyMap());

		// when
		fcmService.sendToAssociates(request);

		// then
		List<FCMToken> remainingTokens = fcmTokenRepository.findAll();
		assertThat(remainingTokens).isEmpty();
	}

	@Test
	@DisplayName("유효하지 않은 FCM 토큰은 삭제한다 - INVALID_ARGUMENT")
	void deleteInvalidTokenWhenInvalidArgument() throws FirebaseMessagingException {
		// given
		FCMTestFixtures fixtures = createSingleReceiverFixtures();
		FCMRequest request = FCMRequest.of(
			ACHIEVE.getTitle(),
			List.of(ReceiverInfo.of(fixtures.receiver.getId(), "새로운 업적을 달성했어요")),
			BasicData.of(ACHIEVE)
		);

		FirebaseMessagingException exception = createFirebaseException(MessagingErrorCode.INVALID_ARGUMENT);
		doThrow(exception).when(fcmSender).send(anyString(), anyString(), anyString(), anyMap());

		// when
		fcmService.sendToAssociates(request);

		// then
		List<FCMToken> remainingTokens = fcmTokenRepository.findAll();
		assertThat(remainingTokens).isEmpty();
	}

	@Test
	@DisplayName("일시적인 전송 실패 시 토큰을 삭제하지 않는다")
	void keepTokenWhenTemporaryFailure() throws FirebaseMessagingException {
		// given
		FCMTestFixtures fixtures = createSingleReceiverFixtures();
		FCMRequest request = FCMRequest.of(
			ACHIEVE.getTitle(),
			List.of(ReceiverInfo.of(fixtures.receiver.getId(), "새로운 업적을 달성했어요")),
			BasicData.of(ACHIEVE)
		);

		FirebaseMessagingException exception = createFirebaseException(MessagingErrorCode.INTERNAL);
		doThrow(exception).when(fcmSender).send(anyString(), anyString(), anyString(), anyMap());

		// when
		fcmService.sendToAssociates(request);

		// then
		List<FCMToken> remainingTokens = fcmTokenRepository.findAll();
		assertThat(remainingTokens).hasSize(1);
		assertThat(remainingTokens.getFirst().getToken()).isEqualTo(fixtures.fcmToken.getToken());
	}

	@Test
	@DisplayName("일부 수신자의 전송 실패는 다른 수신자에게 영향을 주지 않는다")
	void continueWhenPartialFailure() throws FirebaseMessagingException {
		// given
		FCMTestFixtures fixtures = createMultipleReceiverFixtures();
		FCMRequest request = FCMRequest.of(
			REACTION.getTitle(),
			List.of(
				ReceiverInfo.of(fixtures.receiver1.getId(), "포스트에 반응을 남겼어요"),
				ReceiverInfo.of(fixtures.receiver2.getId(), "포스트에 반응을 남겼어요")
			),
			ReactionData.of(1L, 1L)
		);

		// receiver1은 실패, receiver2는 성공
		FirebaseMessagingException exception = createFirebaseException(MessagingErrorCode.UNREGISTERED);
		doThrow(exception)
			.doNothing()
			.when(fcmSender).send(anyString(), anyString(), anyString(), anyMap());

		// when
		fcmService.sendToAssociates(request);

		// then
		verify(fcmSender, times(2)).send(anyString(), anyString(), anyString(), anyMap());

		List<FCMToken> remainingTokens = fcmTokenRepository.findAll();
		assertThat(remainingTokens).hasSize(1);
		assertThat(remainingTokens.getFirst().getToken()).isEqualTo(fixtures.fcmToken2.getToken());
	}

	@Test
	@DisplayName("FCM 토큰을 저장한다")
	void saveFCMToken() {
		// given
		Member member = memberRepository.save(
			Member.create("회원", "member@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Community community = communityRepository.save(Community.create("테스트 커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("참여자", member, community));

		com.memento.server.api.service.fcm.dto.request.SaveFCMTokenServiceRequest request =
			com.memento.server.api.service.fcm.dto.request.SaveFCMTokenServiceRequest.of(
				associate.getId(), "new_fcm_token_12345");

		// when
		fcmService.saveFCMToken(request);

		// then
		List<FCMToken> savedTokens = fcmTokenRepository.findAll();
		assertThat(savedTokens).hasSize(1);
		assertThat(savedTokens.getFirst().getToken()).isEqualTo("new_fcm_token_12345");
		assertThat(savedTokens.getFirst().getAssociate().getId()).isEqualTo(associate.getId());
	}

	@Test
	@DisplayName("중복된 FCM 토큰 저장 시 예외가 발생한다")
	void saveDuplicateFCMToken() {
		// given
		Member member = memberRepository.save(
			Member.create("회원", "member@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Community community = communityRepository.save(Community.create("테스트 커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("참여자", member, community));

		FCMToken existingToken = fcmTokenRepository.save(FCMToken.builder()
			.token("duplicate_token")
			.associate(associate)
			.build());

		com.memento.server.api.service.fcm.dto.request.SaveFCMTokenServiceRequest request =
			com.memento.server.api.service.fcm.dto.request.SaveFCMTokenServiceRequest.of(
				associate.getId(), "duplicate_token");

		// when & then
		assertThatThrownBy(() -> fcmService.saveFCMToken(request))
			.isInstanceOf(com.memento.server.common.exception.MementoException.class)
			.extracting("errorCode")
			.isEqualTo(com.memento.server.common.error.ErrorCodes.FCMTOKEN_DUPLICATE);
	}

	@Test
	@DisplayName("존재하지 않는 Associate로 FCM 토큰 저장 시 예외가 발생한다")
	void saveFCMTokenWithNonExistentAssociate() {
		// given
		Long nonExistentAssociateId = 999L;
		com.memento.server.api.service.fcm.dto.request.SaveFCMTokenServiceRequest request =
			com.memento.server.api.service.fcm.dto.request.SaveFCMTokenServiceRequest.of(
				nonExistentAssociateId, "new_token");

		// when & then
		assertThatThrownBy(() -> fcmService.saveFCMToken(request))
			.isInstanceOf(com.memento.server.common.exception.MementoException.class)
			.extracting("errorCode")
			.isEqualTo(com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_FOUND);
	}

	private FCMTestFixtures createSingleReceiverFixtures() {
		Member member = memberRepository.save(
			Member.create("수신자", "receiver@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Community community = communityRepository.save(Community.create("테스트 커뮤니티", member));
		Associate receiver = associateRepository.save(Associate.create("수신자", member, community));
		FCMToken fcmToken = fcmTokenRepository.save(FCMToken.builder()
			.token("test-fcm-token-1")
			.associate(receiver)
			.build());

		return FCMTestFixtures.builder()
			.receiver(receiver)
			.fcmToken(fcmToken)
			.build();
	}

	private FCMTestFixtures createMultipleReceiverFixtures() {
		Member member1 = memberRepository.save(
			Member.create("수신자1", "receiver1@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(
			Member.create("수신자2", "receiver2@test.com", LocalDate.of(1990, 1, 1), 1002L));

		Community community = communityRepository.save(Community.create("테스트 커뮤니티", member1));

		Associate receiver1 = associateRepository.save(Associate.create("수신자1", member1, community));
		Associate receiver2 = associateRepository.save(Associate.create("수신자2", member2, community));

		FCMToken fcmToken1 = fcmTokenRepository.save(FCMToken.builder()
			.token("test-fcm-token-1")
			.associate(receiver1)
			.build());
		FCMToken fcmToken2 = fcmTokenRepository.save(FCMToken.builder()
			.token("test-fcm-token-2")
			.associate(receiver2)
			.build());

		return FCMTestFixtures.builder()
			.receiver1(receiver1)
			.receiver2(receiver2)
			.fcmToken1(fcmToken1)
			.fcmToken2(fcmToken2)
			.build();
	}

	private FCMTestFixtures createReceiverWithoutToken() {
		Member member = memberRepository.save(
			Member.create("수신자", "receiver@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Community community = communityRepository.save(Community.create("테스트 커뮤니티", member));
		Associate receiver = associateRepository.save(Associate.create("수신자", member, community));

		return FCMTestFixtures.builder()
			.receiver(receiver)
			.build();
	}

	private FirebaseMessagingException createFirebaseException(MessagingErrorCode errorCode) {
		FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
		when(exception.getMessagingErrorCode()).thenReturn(errorCode);
		return exception;
	}

	public static class FCMTestFixtures {
		public Associate receiver;
		public Associate receiver1;
		public Associate receiver2;
		public FCMToken fcmToken;
		public FCMToken fcmToken1;
		public FCMToken fcmToken2;

		public static FCMTestFixturesBuilder builder() {
			return new FCMTestFixturesBuilder();
		}

		public static class FCMTestFixturesBuilder {
			private Associate receiver;
			private Associate receiver1;
			private Associate receiver2;
			private FCMToken fcmToken;
			private FCMToken fcmToken1;
			private FCMToken fcmToken2;

			public FCMTestFixturesBuilder receiver(Associate receiver) {
				this.receiver = receiver;
				return this;
			}

			public FCMTestFixturesBuilder receiver1(Associate receiver1) {
				this.receiver1 = receiver1;
				return this;
			}

			public FCMTestFixturesBuilder receiver2(Associate receiver2) {
				this.receiver2 = receiver2;
				return this;
			}

			public FCMTestFixturesBuilder fcmToken(FCMToken fcmToken) {
				this.fcmToken = fcmToken;
				return this;
			}

			public FCMTestFixturesBuilder fcmToken1(FCMToken fcmToken1) {
				this.fcmToken1 = fcmToken1;
				return this;
			}

			public FCMTestFixturesBuilder fcmToken2(FCMToken fcmToken2) {
				this.fcmToken2 = fcmToken2;
				return this;
			}

			public FCMTestFixtures build() {
				FCMTestFixtures fixtures = new FCMTestFixtures();
				fixtures.receiver = this.receiver;
				fixtures.receiver1 = this.receiver1;
				fixtures.receiver2 = this.receiver2;
				fixtures.fcmToken = this.fcmToken;
				fixtures.fcmToken1 = this.fcmToken1;
				fixtures.fcmToken2 = this.fcmToken2;
				return fixtures;
			}
		}
	}
}
