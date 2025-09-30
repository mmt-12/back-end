package com.memento.server.spring.api.service.eventMessage;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.memento.server.api.service.eventMessage.FCMEventListener;
import com.memento.server.api.service.eventMessage.dto.AchievementFCM;
import com.memento.server.api.service.eventMessage.dto.AssociateFCM;
import com.memento.server.api.service.eventMessage.dto.BirthdayFCM;
import com.memento.server.api.service.eventMessage.dto.GuestBookFCM;
import com.memento.server.api.service.eventMessage.dto.MbtiFCM;
import com.memento.server.api.service.eventMessage.dto.MemoryFCM;
import com.memento.server.api.service.eventMessage.dto.NewImageFCM;
import com.memento.server.api.service.eventMessage.dto.PostFCM;
import com.memento.server.api.service.eventMessage.dto.ReactionFCM;
import com.memento.server.api.service.fcm.FCMService;
import com.memento.server.api.service.fcm.dto.FCMRequest;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.event.Event;
import com.memento.server.domain.event.EventRepository;
import com.memento.server.domain.event.Location;
import com.memento.server.domain.event.Period;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.MemoryAssociate;
import com.memento.server.domain.memory.MemoryAssociateRepository;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationRepository;
import com.memento.server.domain.post.Post;
import com.memento.server.domain.post.PostRepository;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

@ExtendWith(MockitoExtension.class)
public class FCMEventListenerTest extends IntegrationsTestSupport {

	@Autowired
	private FCMEventListener fcmEventListener;

	@Mock
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
	private EventRepository eventRepository;

	@Autowired
	private MemoryAssociateRepository memoryAssociateRepository;

	@Autowired
	private PostRepository postRepository;

	@AfterEach
	public void tearDown() {
		notificationRepository.deleteAllInBatch();
		postRepository.deleteAllInBatch();
		memoryAssociateRepository.deleteAllInBatch();
		memoryRepository.deleteAllInBatch();
		eventRepository.deleteAllInBatch();
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
		fcmEventListener.handleReactionNotification(event);

		// then
		// 1. Notification 생성 검증
		List<Notification> notifications = notificationRepository.findAll();
		assertThat(notifications).hasSize(1);

		Notification notification = notifications.get(0);
		assertThat(notification.getTitle()).isEqualTo(REACTION.getTitle());
		assertThat(notification.getContent()).contains(fixtures.actor.getNickname() + "님이 포스트에 반응을 남겼어요");
		assertThat(notification.getType()).isEqualTo(REACTION);
		assertThat(notification.getReceiver().getId()).isEqualTo(fixtures.receiver.getId());

		// 2. FCMService 호출 검증
		ArgumentCaptor<FCMRequest> captor = ArgumentCaptor.forClass(FCMRequest.class);
		verify(fcmService).sendToAssociates(captor.capture());

		FCMRequest request = captor.getValue();
		assertThat(request.title()).isEqualTo(REACTION.getTitle());
		assertThat(request.receiverInfos()).hasSize(1);
		assertThat(request.receiverInfos().get(0).id()).isEqualTo(fixtures.receiver.getId());
	}

	@Test
	@DisplayName("업적 달성 알림을 생성하고 FCM 전송을 요청한다")
	void handleAchievementNotification() {
		// given
		FCMTestFixtures fixtures = createSingleReceiverTestFixtures();
		AchievementFCM event = AchievementFCM.of(fixtures.receiver.getId());

		// when
		fcmEventListener.handleAchievementNotification(event);

		// then
		// 1. Notification 생성 검증
		List<Notification> notifications = notificationRepository.findAll();
		assertThat(notifications).hasSize(1);

		Notification notification = notifications.get(0);
		assertThat(notification.getTitle()).isEqualTo(ACHIEVE.getTitle());
		assertThat(notification.getContent()).contains(fixtures.receiver.getNickname() + "님, 새로운 업적을 달성했어요");
		assertThat(notification.getType()).isEqualTo(ACHIEVE);

		// 2. FCMService 호출 검증
		verify(fcmService).sendToAssociates(any(FCMRequest.class));
	}

	@Test
	@DisplayName("존재하지 않는 Associate ID로 업적 알림 시 예외가 발생한다")
	void handleAchievementNotificationWithNonExistentAssociate() {
		// given
		AchievementFCM event = AchievementFCM.of(999L);

		// when & then
		assertThatThrownBy(() -> fcmEventListener.handleAchievementNotification(event))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ASSOCIATE_NOT_EXISTENCE);
	}

	@Test
	@DisplayName("방명록 알림을 생성하고 FCM 전송을 요청한다")
	void handleGuestBookNotification() {
		// given
		FCMTestFixtures fixtures = createSingleReceiverTestFixtures();
		GuestBookFCM event = GuestBookFCM.from(fixtures.receiver.getId());

		// when
		fcmEventListener.handleGuestBookNotification(event);

		// then
		// 1. Notification 생성 검증
		List<Notification> notifications = notificationRepository.findAll();
		assertThat(notifications).hasSize(1);

		Notification notification = notifications.get(0);
		assertThat(notification.getTitle()).isEqualTo(GUESTBOOK.getTitle());
		assertThat(notification.getContent()).isEqualTo("누군가가 내 방명록에 글을 작성했어요.");
		assertThat(notification.getType()).isEqualTo(GUESTBOOK);

		// 2. FCMService 호출 검증
		verify(fcmService).sendToAssociates(any(FCMRequest.class));
	}

	@Test
	@DisplayName("새 이미지 알림을 생성하고 FCM 전송을 요청한다")
	void handleNewImageNotification() {
		// given
		FCMTestFixtures fixtures = createSingleReceiverTestFixtures();
		NewImageFCM event = NewImageFCM.from(fixtures.receiver.getId());

		// when
		fcmEventListener.handleNewImageNotification(event);

		// then
		// 1. Notification 생성 검증
		List<Notification> notifications = notificationRepository.findAll();
		assertThat(notifications).hasSize(1);

		Notification notification = notifications.get(0);
		assertThat(notification.getTitle()).isEqualTo(NEWIMAGE.getTitle());
		assertThat(notification.getContent()).isEqualTo("새로운 프로필 이미지가 등록되었습니다.");
		assertThat(notification.getType()).isEqualTo(NEWIMAGE);

		// 2. FCMService 호출 검증
		verify(fcmService).sendToAssociates(any(FCMRequest.class));
	}

	@Test
	@DisplayName("MBTI 알림을 생성하고 FCM 전송을 요청한다")
	void handleMbtiNotification() {
		// given
		FCMTestFixtures fixtures = createSingleReceiverTestFixtures();
		MbtiFCM event = MbtiFCM.from(fixtures.receiver.getId());

		// when
		fcmEventListener.handleMbtiNotification(event);

		// then
		// 1. Notification 생성 검증
		List<Notification> notifications = notificationRepository.findAll();
		assertThat(notifications).hasSize(1);

		Notification notification = notifications.get(0);
		assertThat(notification.getTitle()).isEqualTo(MBTI.getTitle());
		assertThat(notification.getContent()).isEqualTo("새로운 MBTI 평가가 추가되었습니다. 결과를 확인 해보세요.");
		assertThat(notification.getType()).isEqualTo(MBTI);

		// 2. FCMService 호출 검증
		verify(fcmService).sendToAssociates(any(FCMRequest.class));
	}

	@Test
	@DisplayName("생일 알림을 생성하고 FCM 전송을 요청한다")
	void handleBirthdayNotification() {
		// given
		FCMTestFixtures fixtures = createBirthdayTestFixtures();
		BirthdayFCM event = BirthdayFCM.from(fixtures.community.getId(), fixtures.birthdayPerson.getId());

		// when
		fcmEventListener.handleBirthdayNotification(event);

		// then
		// 1. Notification 생성 검증 (생일자 + 다른 멤버들)
		List<Notification> notifications = notificationRepository.findAll();
		assertThat(notifications).hasSize(3); // 생일자 1명 + 다른 멤버 2명

		// 생일자가 아닌 멤버들의 알림 검증
		List<Notification> associateNotifications = notifications.stream()
			.filter(n -> !n.getReceiver().equals(fixtures.birthdayPerson))
			.toList();
		assertThat(associateNotifications).hasSize(2);
		assertThat(associateNotifications.get(0).getContent())
			.contains("오늘은 " + fixtures.birthdayPerson.getNickname() + "님의 생일입니다");

		// 생일자의 알림 검증
		Notification birthdayNotification = notifications.stream()
			.filter(n -> n.getReceiver().equals(fixtures.birthdayPerson))
			.findFirst()
			.orElseThrow();
		assertThat(birthdayNotification.getContent())
			.contains(fixtures.birthdayPerson.getNickname() + "님 생일 축하드립니다");

		// 2. FCMService 호출 검증
		verify(fcmService).sendToAssociates(any(FCMRequest.class));
	}

	@Test
	@DisplayName("존재하지 않는 생일자 ID로 생일 알림 시 예외가 발생한다")
	void handleBirthdayNotificationWithNonExistentBirthdayPerson() {
		// given
		FCMTestFixtures fixtures = createBirthdayTestFixtures();
		BirthdayFCM event = BirthdayFCM.from(fixtures.community.getId(), 999L);

		// when & then
		assertThatThrownBy(() -> fcmEventListener.handleBirthdayNotification(event))
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
		fcmEventListener.handleMemoryNotification(event);

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
		ArgumentCaptor<FCMRequest> captor = ArgumentCaptor.forClass(FCMRequest.class);
		verify(fcmService).sendToAssociates(captor.capture());

		FCMRequest request = captor.getValue();
		assertThat(request.receiverInfos()).hasSize(2);
	}

	@Test
	@DisplayName("포스트 생성 알림을 생성하고 FCM 전송을 요청한다")
	void handlePostNotification() {
		// given
		FCMTestFixtures fixtures = createPostTestFixtures();
		PostFCM event = PostFCM.of(fixtures.actor.getId(), fixtures.memory.getId(), fixtures.post.getId());

		// when
		fcmEventListener.handlePostNotification(event);

		// then
		// 1. Notification 생성 검증
		List<Notification> notifications = notificationRepository.findAll();
		assertThat(notifications).hasSize(2); // 포스트 작성자 제외

		notifications.forEach(notification -> {
			assertThat(notification.getTitle()).isEqualTo(POST.getTitle());
			assertThat(notification.getContent()).contains(fixtures.event.getTitle() + "에 새로운 포스트가 올라왔어요");
			assertThat(notification.getType()).isEqualTo(POST);
		});

		// 2. FCMService 호출 검증
		verify(fcmService).sendToAssociates(any(FCMRequest.class));
	}

	@Test
	@DisplayName("존재하지 않는 메모리 ID로 포스트 알림 시 예외가 발생한다")
	void handlePostNotificationWithNonExistentMemory() {
		// given
		PostFCM event = PostFCM.of(1L, 999L, 1L);

		// when & then
		assertThatThrownBy(() -> fcmEventListener.handlePostNotification(event))
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
		fcmEventListener.handleAssociateNotification(event);

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
		verify(fcmService).sendToAssociates(any(FCMRequest.class));
	}

	// Test Fixtures
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

		Event event = eventRepository.save(createTestEvent(community, actor));
		Memory memory = memoryRepository.save(Memory.builder().event(event).build());
		Post post = postRepository.save(Post.builder()
			.content("테스트 포스트")
			.memory(memory)
			.associate(receiver)
			.build());

		return FCMTestFixtures.builder()
			.actor(actor)
			.receiver(receiver)
			.community(community)
			.event(event)
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

		Event event = eventRepository.save(createTestEvent(community, actor));
		Memory memory = memoryRepository.save(Memory.builder().event(event).build());

		// MemoryAssociate 관계 생성
		memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(actor).build());
		memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(member1Associate).build());
		memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(member2Associate).build());

		return FCMTestFixtures.builder()
			.actor(actor)
			.community(community)
			.event(event)
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

	private Event createTestEvent(Community community, Associate associate) {
		return Event.builder()
			.title("테스트 이벤트")
			.description("테스트 설명")
			.location(Location.builder()
				.address("테스트 주소")
				.name("테스트 장소")
				.latitude(BigDecimal.valueOf(37.5665))
				.longitude(BigDecimal.valueOf(126.9780))
				.code(1)
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.now().minusDays(1))
				.endTime(LocalDateTime.now())
				.build())
			.community(community)
			.associate(associate)
			.build();
	}

	// Test Fixtures Builder
	public static class FCMTestFixtures {
		public Associate actor;
		public Associate receiver;
		public Associate birthdayPerson;
		public Associate member1;
		public Associate member2;
		public Associate newMember;
		public Community community;
		public Event event;
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
				.event(this.event)
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
			private Event event;
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

			public FCMTestFixturesBuilder event(Event event) {
				this.event = event;
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
				fixtures.event = this.event;
				fixtures.memory = this.memory;
				fixtures.post = this.post;
				return fixtures;
			}
		}
	}
}