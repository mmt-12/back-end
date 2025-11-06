package com.memento.server.spring.api.service.fcm;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_EXISTENCE;
import static com.memento.server.common.error.ErrorCodes.MEMORY_NOT_FOUND;
import static com.memento.server.domain.notification.NotificationType.ACHIEVE;
import static com.memento.server.domain.notification.NotificationType.ASSOCIATE;
import static com.memento.server.domain.notification.NotificationType.BIRTHDAY;
import static com.memento.server.domain.notification.NotificationType.GUESTBOOK;
import static com.memento.server.domain.notification.NotificationType.MBTI;
import static com.memento.server.domain.notification.NotificationType.MEMORY;
import static com.memento.server.domain.notification.NotificationType.NEWIMAGE;
import static com.memento.server.domain.notification.NotificationType.POST;
import static com.memento.server.domain.notification.NotificationType.REACTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.memento.server.api.service.fcm.FCMEventHandler;
import com.memento.server.api.service.fcm.dto.event.AchievementFCM;
import com.memento.server.api.service.fcm.dto.event.AssociateFCM;
import com.memento.server.api.service.fcm.dto.event.BirthdayFCM;
import com.memento.server.api.service.fcm.dto.event.GuestBookFCM;
import com.memento.server.api.service.fcm.dto.event.MbtiFCM;
import com.memento.server.api.service.fcm.dto.event.MemoryFCM;
import com.memento.server.api.service.fcm.dto.event.NewImageFCM;
import com.memento.server.api.service.fcm.dto.event.PostFCM;
import com.memento.server.api.service.fcm.dto.event.ReactionFCM;
import com.memento.server.api.service.fcm.FCMService;
import com.memento.server.api.service.fcm.dto.request.FCMRequest;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.Location;
import com.memento.server.domain.memory.Period;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.memory.MemoryAssociate;
import com.memento.server.domain.memory.MemoryAssociateRepository;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationRepository;
import com.memento.server.domain.notification.NotificationType;
import com.memento.server.domain.post.Post;
import com.memento.server.domain.post.PostRepository;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

public class FCMMemoryHandlerTest extends IntegrationsTestSupport {

	@Autowired
	private FCMEventHandler fcmEventHandler;

	@MockitoBean
	private FCMService fcmService;

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private MemoryRepository memoryRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private MemoryAssociateRepository memoryAssociateRepository;

	@Autowired
	private PostRepository postRepository;

	private FCMTestFixtures commonFixtures;

	@BeforeEach
	public void setUp() {
		commonFixtures = createSingleReceiverTestFixtures();
	}

	@AfterEach
	public void tearDown() {
		notificationRepository.deleteAllInBatch();
		postRepository.deleteAllInBatch();
		memoryAssociateRepository.deleteAllInBatch();
		memoryRepository.deleteAllInBatch();
		associateRepository.deleteAllInBatch();
		communityRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("리액션 알림을 생성하고 FCM 전송을 요청한다")
	void handleReactionNotification() {
		// given
		FCMTestFixtures fixtures = createReactionTestFixtures();
		ReactionFCM event = ReactionFCM.of(
			fixtures.actor.getNickname(),
			fixtures.actor.getId(),
			fixtures.memory.getId(),
			fixtures.post.getId(),
			fixtures.receiver.getId()
		);

		// when
		fcmEventHandler.handleReactionNotification(event);

		// then
		assertSingleNotification(REACTION, fixtures.actor.getNickname() + "님이 포스트에 반응을 남겼어요");
		assertSingleReceiverFCMRequest(REACTION, fixtures.receiver.getId());
	}

	@Test
	@DisplayName("업적 달성 알림을 생성하고 FCM 전송을 요청한다")
	void handleAchievementNotification() {
		// given
		AchievementFCM event = AchievementFCM.of(commonFixtures.receiver.getId());

		// when
		fcmEventHandler.handleAchievementNotification(event);

		// then
		assertSingleNotification(ACHIEVE, commonFixtures.receiver.getNickname() + "님, 새로운 업적을 달성했어요");
		assertSingleReceiverFCMRequest(ACHIEVE, commonFixtures.receiver.getId());
	}

	@Test
	@DisplayName("존재하지 않는 Associate ID로 업적 알림 시 예외가 발생한다")
	void handleAchievementNotificationWithNonExistentAssociate() {
		// given
		AchievementFCM event = AchievementFCM.of(999L);

		// when & then
		assertThatThrownBy(() -> fcmEventHandler.handleAchievementNotification(event))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ASSOCIATE_NOT_EXISTENCE);
	}

	@Test
	@DisplayName("방명록 알림을 생성하고 FCM 전송을 요청한다")
	void handleGuestBookNotification() {
		// given
		GuestBookFCM event = GuestBookFCM.from(commonFixtures.receiver.getId());

		// when
		fcmEventHandler.handleGuestBookNotification(event);

		// then
		assertSingleNotification(GUESTBOOK, "누군가가 내 방명록에 글을 작성했어요");
		assertSingleReceiverFCMRequest(GUESTBOOK, commonFixtures.receiver.getId());
	}

	@Test
	@DisplayName("새 이미지 알림을 생성하고 FCM 전송을 요청한다")
	void handleNewImageNotification() {
		// given
		NewImageFCM event = NewImageFCM.from(commonFixtures.receiver.getId());

		// when
		fcmEventHandler.handleNewImageNotification(event);

		// then
		assertSingleNotification(NEWIMAGE, "새로운 프로필 이미지가 등록되었습니다");
		assertSingleReceiverFCMRequest(NEWIMAGE, commonFixtures.receiver.getId());
	}

	@Test
	@DisplayName("MBTI 알림을 생성하고 FCM 전송을 요청한다")
	void handleMbtiNotification() {
		// given
		MbtiFCM event = MbtiFCM.from(commonFixtures.receiver.getId());

		// when
		fcmEventHandler.handleMbtiNotification(event);

		// then
		assertSingleNotification(MBTI, "새로운 MBTI 평가가 추가되었습니다. 결과를 확인 해보세요");
		assertSingleReceiverFCMRequest(MBTI, commonFixtures.receiver.getId());
	}

	@Test
	@DisplayName("생일 알림을 생성하고 FCM 전송을 요청한다")
	void handleBirthdayNotification() {
		// given
		FCMTestFixtures fixtures = createBirthdayTestFixtures();
		BirthdayFCM event = BirthdayFCM.from(fixtures.community.getId(), fixtures.birthdayPerson.getId());

		// when
		fcmEventHandler.handleBirthdayNotification(event);

		// then
		// 1. Notification 생성 검증 (생일자 + 다른 멤버들)
		List<Notification> notifications = notificationRepository.findAll();
		assertThat(notifications).hasSize(3); // 생일자 1명 + 다른 멤버 2명

		// 생일자가 아닌 멤버들의 알림 검증
		List<Notification> associateNotifications = notifications.stream()
			.filter(n -> !n.getReceiver().getId().equals(fixtures.birthdayPerson.getId()))
			.toList();
		assertThat(associateNotifications).hasSize(2);
		assertThat(associateNotifications.getFirst().getContent())
			.contains("오늘은 " + fixtures.birthdayPerson.getNickname() + "님의 생일입니다");

		// 생일자의 알림 검증
		Notification birthdayNotification = notifications.stream()
			.filter(n -> n.getReceiver().getId().equals(fixtures.birthdayPerson.getId()))
			.findFirst()
			.orElseThrow();
		assertThat(birthdayNotification.getContent())
			.contains(fixtures.birthdayPerson.getNickname() + "님 생일 축하드립니다");

		// 2. FCMService 호출 검증
		assertMultipleReceiverFCMRequest(BIRTHDAY, 3);
	}

	@Test
	@DisplayName("존재하지 않는 생일자 ID로 생일 알림 시 예외가 발생한다")
	void handleBirthdayNotificationWithNonExistentBirthdayPerson() {
		// given
		FCMTestFixtures fixtures = createBirthdayTestFixtures();
		BirthdayFCM event = BirthdayFCM.from(fixtures.community.getId(), 999L);

		// when & then
		assertThatThrownBy(() -> fcmEventHandler.handleBirthdayNotification(event))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ASSOCIATE_NOT_EXISTENCE);
	}

	@Test
	@DisplayName("메모리 생성 알림을 생성하고 FCM 전송을 요청한다")
	void handleMemoryNotification() {
		// given
		FCMTestFixtures fixtures = createMemoryTestFixtures();
		MemoryFCM event = MemoryFCM.from(fixtures.memory.getId(), fixtures.actor.getId());

		// when
		fcmEventHandler.handleMemoryNotification(event);

		// then
		// 1. Notification 생성 검증 (메모리 생성자 제외)
		List<Notification> notifications = notificationRepository.findAll();
		assertThat(notifications).hasSize(2); // 전체 3명 - 생성자 1명

		notifications.forEach(notification -> {
			assertThat(notification.getTitle()).isEqualTo(MEMORY.getTitle());
			assertThat(notification.getContent()).contains("님이 참가한 새로운 기억이 추가되었어요");
			assertThat(notification.getType()).isEqualTo(MEMORY);
			assertThat(notification.getReceiver().getId()).isNotEqualTo(fixtures.actor.getId());
		});

		// 2. FCMService 호출 검증
		assertMultipleReceiverFCMRequest(MEMORY, 2);
	}

	@Test
	@DisplayName("포스트 생성 알림을 생성하고 FCM 전송을 요청한다")
	void handlePostNotification() {
		// given
		FCMTestFixtures fixtures = createPostTestFixtures();
		PostFCM event = PostFCM.of(fixtures.actor.getId(), fixtures.memory.getId(), fixtures.post.getId());

		// when
		fcmEventHandler.handlePostNotification(event);

		// then
		// 1. Notification 생성 검증
		List<Notification> notifications = notificationRepository.findAll();
		assertThat(notifications).hasSize(2); // 포스트 작성자 제외

		notifications.forEach(notification -> {
			assertThat(notification.getTitle()).isEqualTo(POST.getTitle());
			assertThat(notification.getContent()).contains(fixtures.memory.getTitle() + "에 새로운 포스트가 올라왔어요");
			assertThat(notification.getType()).isEqualTo(POST);
		});

		// 2. FCMService 호출 검증
		assertMultipleReceiverFCMRequest(POST, 2);
	}

	@Test
	@DisplayName("존재하지 않는 메모리 ID로 포스트 알림 시 예외가 발생한다")
	void handlePostNotificationWithNonExistentMemory() {
		// given
		PostFCM event = PostFCM.of(1L, 999L, 1L);

		// when & then
		assertThatThrownBy(() -> fcmEventHandler.handlePostNotification(event))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", MEMORY_NOT_FOUND);
	}

	@Test
	@DisplayName("새 참가자 알림을 생성하고 FCM 전송을 요청한다")
	void handleAssociateNotification() {
		// given
		FCMTestFixtures fixtures = createAssociateTestFixtures();
		AssociateFCM event = AssociateFCM.from("새멤버", fixtures.community.getId(), fixtures.newMember.getId());

		// when
		fcmEventHandler.handleAssociateNotification(event);

		// then
		// 1. Notification 생성 검증 (새 참가자 제외)
		List<Notification> notifications = notificationRepository.findAll();
		assertThat(notifications).hasSize(2); // 기존 멤버 2명

		notifications.forEach(notification -> {
			assertThat(notification.getTitle()).isEqualTo(ASSOCIATE.getTitle());
			assertThat(notification.getContent()).contains("새멤버님이 가입했어요");
			assertThat(notification.getType()).isEqualTo(ASSOCIATE);
		});

		// 2. FCMService 호출 검증
		assertMultipleReceiverFCMRequest(ASSOCIATE, 2);
	}

	@Test
	@DisplayName("존재하지 않는 Associate ID로 리액션 알림 시 예외가 발생한다")
	void handleReactionNotificationWithNonExistentAssociate() {
		// given
		ReactionFCM event = ReactionFCM.of("actor", 1L, 1L, 1L, 999L);

		// when & then
		assertThatThrownBy(() -> fcmEventHandler.handleReactionNotification(event))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ASSOCIATE_NOT_EXISTENCE);
	}

	@Test
	@DisplayName("존재하지 않는 Associate ID로 방명록 알림 시 예외가 발생한다")
	void handleGuestBookNotificationWithNonExistentAssociate() {
		// given
		GuestBookFCM event = GuestBookFCM.from(999L);

		// when & then
		assertThatThrownBy(() -> fcmEventHandler.handleGuestBookNotification(event))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ASSOCIATE_NOT_EXISTENCE);
	}

	@Test
	@DisplayName("존재하지 않는 Associate ID로 새 이미지 알림 시 예외가 발생한다")
	void handleNewImageNotificationWithNonExistentAssociate() {
		// given
		NewImageFCM event = NewImageFCM.from(999L);

		// when & then
		assertThatThrownBy(() -> fcmEventHandler.handleNewImageNotification(event))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ASSOCIATE_NOT_EXISTENCE);
	}

	@Test
	@DisplayName("존재하지 않는 Associate ID로 MBTI 알림 시 예외가 발생한다")
	void handleMbtiNotificationWithNonExistentAssociate() {
		// given
		MbtiFCM event = MbtiFCM.from(999L);

		// when & then
		assertThatThrownBy(() -> fcmEventHandler.handleMbtiNotification(event))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ASSOCIATE_NOT_EXISTENCE);
	}

	private void assertSingleNotification(NotificationType type, String expectedContent) {
		List<Notification> notifications = notificationRepository.findAll();
		assertThat(notifications).hasSize(1);

		Notification notification = notifications.getFirst();
		assertThat(notification.getTitle()).isEqualTo(type.getTitle());
		assertThat(notification.getContent()).contains(expectedContent);
		assertThat(notification.getType()).isEqualTo(type);
	}

	private void assertSingleReceiverFCMRequest(NotificationType type, Long receiverId) {
		ArgumentCaptor<FCMRequest> captor = ArgumentCaptor.forClass(FCMRequest.class);

		verify(fcmService).sendToAssociates(captor.capture());

		FCMRequest request = captor.getValue();
		assertThat(request.title()).isEqualTo(type.getTitle());
		assertThat(request.receiverInfos()).hasSize(1);
		assertThat(request.receiverInfos().getFirst().id()).isEqualTo(receiverId);
	}

	private void assertMultipleReceiverFCMRequest(NotificationType type, int receiverCount) {
		ArgumentCaptor<FCMRequest> captor = ArgumentCaptor.forClass(FCMRequest.class);

		verify(fcmService).sendToAssociates(captor.capture());

		FCMRequest request = captor.getValue();
		assertThat(request.title()).isEqualTo(type.getTitle());
		assertThat(request.receiverInfos()).hasSize(receiverCount);
	}

	private FCMTestFixtures createSingleReceiverTestFixtures() {
		Member member = memberRepository.save(
			Member.create("테스터", "test@example.com", LocalDate.of(1990, 1, 1), 1001L));
		Community community = communityRepository.save(Community.create("테스트 커뮤니티", member));
		Associate receiver = associateRepository.save(Associate.create("수신자", member, community));

		return FCMTestFixtures.builder()
			.receiver(receiver)
			.community(community)
			.build();
	}

	private FCMTestFixtures createReactionTestFixtures() {
		Member member1 = memberRepository.save(
			Member.create("액터", "actor@example.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(
			Member.create("수신자", "receiver@example.com", LocalDate.of(1990, 1, 1), 1002L));
		Community community = communityRepository.save(Community.create("테스트 커뮤니티", member1));
		Associate actor = associateRepository.save(Associate.create("액터", member1, community));
		Associate receiver = associateRepository.save(Associate.create("수신자", member2, community));

		Memory memory = memoryRepository.save(createTestEvent(community, actor));
		Post post = postRepository.save(Post.builder()
			.content("테스트 포스트")
			.memory(memory)
			.associate(receiver)
			.build());

		return FCMTestFixtures.builder()
			.actor(actor)
			.receiver(receiver)
			.community(community)
			.memory(memory)
			.post(post)
			.build();
	}

	private FCMTestFixtures createBirthdayTestFixtures() {
		Member member1 = memberRepository.save(
			Member.create("생일자", "birthday@example.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(
			Member.create("멤버1", "member1@example.com", LocalDate.of(1990, 1, 1), 1002L));
		Member member3 = memberRepository.save(
			Member.create("멤버2", "member2@example.com", LocalDate.of(1990, 1, 1), 1003L));

		Community community = communityRepository.save(Community.create("테스트 커뮤니티", member1));
		Associate birthdayPerson = associateRepository.save(Associate.create("생일자", member1, community));
		Associate member1Associate = associateRepository.save(Associate.create("멤버1", member2, community));
		Associate member2Associate = associateRepository.save(Associate.create("멤버2", member3, community));

		return FCMTestFixtures.builder()
			.birthdayPerson(birthdayPerson)
			.community(community)
			.member1(member1Associate)
			.member2(member2Associate)
			.build();
	}

	private FCMTestFixtures createMemoryTestFixtures() {
		Member member1 = memberRepository.save(
			Member.create("메모리생성자", "creator@example.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(
			Member.create("멤버1", "member1@example.com", LocalDate.of(1990, 1, 1), 1002L));
		Member member3 = memberRepository.save(
			Member.create("멤버2", "member2@example.com", LocalDate.of(1990, 1, 1), 1003L));

		Community community = communityRepository.save(Community.create("테스트 커뮤니티", member1));
		Associate actor = associateRepository.save(Associate.create("생성자", member1, community));
		Associate member1Associate = associateRepository.save(Associate.create("멤버1", member2, community));
		Associate member2Associate = associateRepository.save(Associate.create("멤버2", member3, community));

		Memory memory = memoryRepository.save(createTestEvent(community, actor));

		memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(actor).build());
		memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(member1Associate).build());
		memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(member2Associate).build());

		return FCMTestFixtures.builder()
			.actor(actor)
			.community(community)
			.memory(memory)
			.member1(member1Associate)
			.member2(member2Associate)
			.build();
	}

	private FCMTestFixtures createPostTestFixtures() {
		FCMTestFixtures fixtures = createMemoryTestFixtures();
		Post post = postRepository.save(Post.builder()
			.content("테스트 포스트")
			.memory(fixtures.memory)
			.associate(fixtures.actor)
			.build());

		return fixtures.toBuilder()
			.post(post)
			.build();
	}

	private FCMTestFixtures createAssociateTestFixtures() {
		Member member1 = memberRepository.save(
			Member.create("기존멤버1", "existing1@example.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(
			Member.create("기존멤버2", "existing2@example.com", LocalDate.of(1990, 1, 1), 1002L));
		Member member3 = memberRepository.save(
			Member.create("새멤버", "new@example.com", LocalDate.of(1990, 1, 1), 1003L));

		Community community = communityRepository.save(Community.create("테스트 커뮤니티", member1));
		Associate existing1 = associateRepository.save(Associate.create("기존1", member1, community));
		Associate existing2 = associateRepository.save(Associate.create("기존2", member2, community));
		Associate newMember = associateRepository.save(Associate.create("새멤버", member3, community));

		return FCMTestFixtures.builder()
			.community(community)
			.member1(existing1)
			.member2(existing2)
			.newMember(newMember)
			.build();
	}

	private Memory createTestEvent(Community community, Associate associate) {
		return Memory.builder()
			.title("테스트 이벤트")
			.description("테스트 설명")
			.location(Location.builder()
				.address("테스트 주소")
				.name("테스트 장소")
				.latitude(BigDecimal.ONE)
				.longitude(BigDecimal.ONE)
				.code(1)
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.of(2025, 1, 1, 0, 0))
				.endTime(LocalDateTime.of(2025, 1, 2, 0, 0))
				.build())
			.community(community)
			.associate(associate)
			.build();
	}

	public static class FCMTestFixtures {
		public Associate actor;
		public Associate receiver;
		public Associate birthdayPerson;
		public Associate member1;
		public Associate member2;
		public Associate newMember;
		public Community community;
		public Memory memory;
		public Post post;

		public static FCMTestFixturesBuilder builder() {
			return new FCMTestFixturesBuilder();
		}

		public FCMTestFixturesBuilder toBuilder() {
			return new FCMTestFixturesBuilder()
				.actor(this.actor)
				.receiver(this.receiver)
				.birthdayPerson(this.birthdayPerson)
				.member1(this.member1)
				.member2(this.member2)
				.newMember(this.newMember)
				.community(this.community)
				.event(this.memory)
				.memory(this.memory)
				.post(this.post);
		}

		public static class FCMTestFixturesBuilder {
			private Associate actor;
			private Associate receiver;
			private Associate birthdayPerson;
			private Associate member1;
			private Associate member2;
			private Associate newMember;
			private Community community;
			private Memory memory;
			private Post post;

			public FCMTestFixturesBuilder actor(Associate actor) {
				this.actor = actor;
				return this;
			}

			public FCMTestFixturesBuilder receiver(Associate receiver) {
				this.receiver = receiver;
				return this;
			}

			public FCMTestFixturesBuilder birthdayPerson(Associate birthdayPerson) {
				this.birthdayPerson = birthdayPerson;
				return this;
			}

			public FCMTestFixturesBuilder member1(Associate member1) {
				this.member1 = member1;
				return this;
			}

			public FCMTestFixturesBuilder member2(Associate member2) {
				this.member2 = member2;
				return this;
			}

			public FCMTestFixturesBuilder newMember(Associate newMember) {
				this.newMember = newMember;
				return this;
			}

			public FCMTestFixturesBuilder community(Community community) {
				this.community = community;
				return this;
			}

			public FCMTestFixturesBuilder event(Memory memory) {
				this.memory = memory;
				return this;
			}

			public FCMTestFixturesBuilder memory(Memory memory) {
				this.memory = memory;
				return this;
			}

			public FCMTestFixturesBuilder post(Post post) {
				this.post = post;
				return this;
			}

			public FCMTestFixtures build() {
				FCMTestFixtures fixtures = new FCMTestFixtures();
				fixtures.actor = this.actor;
				fixtures.receiver = this.receiver;
				fixtures.birthdayPerson = this.birthdayPerson;
				fixtures.member1 = this.member1;
				fixtures.member2 = this.member2;
				fixtures.newMember = this.newMember;
				fixtures.community = this.community;
				fixtures.memory = this.memory;
				fixtures.post = this.post;
				return fixtures;
			}
		}
	}
}