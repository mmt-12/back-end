package com.memento.server.spring.api.service.eventMessage;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.memento.server.api.service.eventMessage.FCMEventListener;
import com.memento.server.api.service.eventMessage.dto.AssociateFCM;
import com.memento.server.api.service.eventMessage.dto.GuestBookFCM;
import com.memento.server.api.service.eventMessage.dto.MbtiFCM;
import com.memento.server.api.service.eventMessage.dto.MemoryFCM;
import com.memento.server.api.service.eventMessage.dto.NewImageFCM;
import com.memento.server.api.service.eventMessage.dto.PostFCM;
import com.memento.server.api.service.eventMessage.dto.ReactionFCM;
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

public class FCMEventListenerTest extends IntegrationsTestSupport {

	@Autowired
	private FCMEventListener FCMEventListener;

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
		memberRepository.deleteAllInBatch();
		associateRepository.deleteAllInBatch();
		memoryRepository.deleteAllInBatch();
		communityRepository.deleteAllInBatch();
		eventRepository.deleteAllInBatch();
		memoryAssociateRepository.deleteAllInBatch();
		postRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("리액션 알림 이벤트를 데이터베이스에 저장한다.")
	void handleReactionNotification() {
		// given
		Member member = memberRepository.save(Member.create("김가가", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(Member.create("김나나", "muge@test.com", LocalDate.of(1990, 1, 1), 1002L));
		Member member3 = memberRepository.save(Member.create("김다다", "muge@test.com", LocalDate.of(1990, 1, 1), 1003L));
		Community community = communityRepository.save(Community.create("comm", member));
		Associate associate = associateRepository.save(Associate.create("가가", member, community));
		Associate associate2 = associateRepository.save(Associate.create("나나", member2, community));
		Associate associate3 = associateRepository.save(Associate.create("다다", member3, community));
		Event event = eventRepository.save(
			Event.builder()
				.title("추억1")
				.description("내용1")
				.location(Location.builder()
					.address("주소1")
					.name("장소1")
					.latitude(BigDecimal.valueOf(1.0))
					.longitude(BigDecimal.valueOf(1.0))
					.code(1)
					.build())
				.period(Period.builder()
					.startTime(LocalDateTime.now().minusDays(2))
					.endTime(LocalDateTime.now().minusDays(1))
					.build())
				.community(community)
				.associate(associate)
				.build());
		Memory memory = memoryRepository.save(Memory.builder().event(event).build());
		memoryAssociateRepository.save(MemoryAssociate.builder()
			.memory(memory)
			.associate(associate)
			.build());
		memoryAssociateRepository.save(MemoryAssociate.builder()
			.memory(memory)
			.associate(associate2)
			.build());
		memoryAssociateRepository.save(MemoryAssociate.builder()
			.memory(memory)
			.associate(associate3)
			.build());
		Post post = postRepository.save(Post.builder()
			.content("content")
			.memory(memory)
			.associate(associate)
			.build());

		// when
		ReactionFCM eventMessage = ReactionFCM.from(post.getId());
		FCMEventListener.handleReactionNotification(eventMessage);

		// then
		List<Notification> all = notificationRepository.findAll();
		assertThat(all.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("포스트 생성 알림 이벤트를 데이터베이스에 저장한다.")
	void handlePostNotification() {
		// given
		Member member = memberRepository.save(Member.create("김가가", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(Member.create("김나나", "muge@test.com", LocalDate.of(1990, 1, 1), 1002L));
		Member member3 = memberRepository.save(Member.create("김다다", "muge@test.com", LocalDate.of(1990, 1, 1), 1003L));
		Community community = communityRepository.save(Community.create("comm", member));
		Associate associate = associateRepository.save(Associate.create("가가", member, community));
		Associate associate2 = associateRepository.save(Associate.create("나나", member2, community));
		Associate associate3 = associateRepository.save(Associate.create("다다", member3, community));
		Event event = eventRepository.save(
			Event.builder()
				.title("추억1")
				.description("내용1")
				.location(Location.builder()
					.address("주소1")
					.name("장소1")
					.latitude(BigDecimal.valueOf(1.0))
					.longitude(BigDecimal.valueOf(1.0))
					.code(1)
					.build())
				.period(Period.builder()
					.startTime(LocalDateTime.now().minusDays(2))
					.endTime(LocalDateTime.now().minusDays(1))
					.build())
				.community(community)
				.associate(associate)
				.build());
		Memory memory = memoryRepository.save(Memory.builder().event(event).build());
		memoryAssociateRepository.save(MemoryAssociate.builder()
			.memory(memory)
			.associate(associate)
			.build());
		memoryAssociateRepository.save(MemoryAssociate.builder()
			.memory(memory)
			.associate(associate2)
			.build());
		memoryAssociateRepository.save(MemoryAssociate.builder()
			.memory(memory)
			.associate(associate3)
			.build());
		Post post = postRepository.save(Post.builder()
			.content("content")
			.memory(memory)
			.associate(associate)
			.build());

		// when
		PostFCM eventMessage = PostFCM.of(memory.getId(), associate.getId(), post.getId());
		FCMEventListener.handlePostNotification(eventMessage);

		// then
		List<Notification> all = notificationRepository.findAll();
		assertThat(all.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("프로필 이미지 등록 알림 이벤트를 데이터베이스에 저장한다.")
	void handleGuestBookNotification() {
		// given
		Member member = memberRepository.save(Member.create("김가가", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Community community = communityRepository.save(Community.create("comm", member));
		Associate associate = associateRepository.save(Associate.create("가가", member, community));

		// when
		GuestBookFCM eventMessage = GuestBookFCM.from(associate.getId());
		FCMEventListener.handleGuestBookNotification(eventMessage);

		// then
		List<Notification> all = notificationRepository.findAll();
		assertThat(all.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("프로필 이미지 등록 알림 이벤트를 데이터베이스에 저장한다.")
	void handleNewImageNotification() {
		// given
		Member member = memberRepository.save(Member.create("김가가", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Community community = communityRepository.save(Community.create("comm", member));
		Associate associate = associateRepository.save(Associate.create("가가", member, community));

		// when
		NewImageFCM eventMessage = NewImageFCM.from(associate.getId());
		FCMEventListener.handleNewImageNotification(eventMessage);

		// then
		List<Notification> all = notificationRepository.findAll();
		assertThat(all.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("MBTI 알림 이벤트를 데이터베이스에 저장한다.")
	void handleMbtiNotification() {
		// given
		Member member = memberRepository.save(Member.create("김가가", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Community community = communityRepository.save(Community.create("comm", member));
		Associate associate = associateRepository.save(Associate.create("가가", member, community));

		// when
		MbtiFCM eventMessage = MbtiFCM.from(associate.getId());
		FCMEventListener.handleMbtiNotification(eventMessage);

		// then
		List<Notification> all = notificationRepository.findAll();
		assertThat(all.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("참가자 추가 알림 이벤트를 데이터베이스에 저장한다.")
	void handleAssociateNotification() {
		// given
		Member member = memberRepository.save(Member.create("김가가", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(Member.create("김나나", "muge@test.com", LocalDate.of(1990, 1, 1), 1002L));
		Member member3 = memberRepository.save(Member.create("김다다", "muge@test.com", LocalDate.of(1990, 1, 1), 1003L));
		Community community = communityRepository.save(Community.create("comm", member));
		associateRepository.save(Associate.create("가가", member, community));
		associateRepository.save(Associate.create("가가", member2, community));

		// when
		AssociateFCM eventMessage = AssociateFCM.from(community.getId(), member3.getId());
		FCMEventListener.handleAssociateNotification(eventMessage);

		// then
		List<Notification> all = notificationRepository.findAll();
		assertThat(all.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("기억 생성 알림 이벤트를 데이터베이스에 저장한다.")
	void handleMemoryNotification() {
		// given
		Member member = memberRepository.save(Member.create("김가가", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(Member.create("김나나", "muge@test.com", LocalDate.of(1990, 1, 1), 1002L));
		Member member3 = memberRepository.save(Member.create("김다다", "muge@test.com", LocalDate.of(1990, 1, 1), 1003L));
		Community community = communityRepository.save(Community.create("comm", member));
		Associate associate = associateRepository.save(Associate.create("가가", member, community));
		Associate associate2 = associateRepository.save(Associate.create("나나", member2, community));
		Associate associate3 = associateRepository.save(Associate.create("다다", member3, community));
		Event event = eventRepository.save(
			Event.builder()
				.title("추억1")
				.description("내용1")
				.location(Location.builder()
					.address("주소1")
					.name("장소1")
					.latitude(BigDecimal.valueOf(1.0))
					.longitude(BigDecimal.valueOf(1.0))
					.code(1)
					.build())
				.period(Period.builder()
					.startTime(LocalDateTime.now().minusDays(2))
					.endTime(LocalDateTime.now().minusDays(1))
					.build())
				.community(community)
				.associate(associate)
				.build());
		Memory memory = memoryRepository.save(Memory.builder().event(event).build());
		memoryAssociateRepository.save(MemoryAssociate.builder()
			.memory(memory)
			.associate(associate)
			.build());
		memoryAssociateRepository.save(MemoryAssociate.builder()
			.memory(memory)
			.associate(associate2)
			.build());
		memoryAssociateRepository.save(MemoryAssociate.builder()
			.memory(memory)
			.associate(associate3)
			.build());

		// when
		MemoryFCM eventMessage = MemoryFCM.from(memory.getId(), associate.getId());
		FCMEventListener.handleMemoryNotification(eventMessage);

		// then
		List<Notification> all = notificationRepository.findAll();
		assertThat(all.size()).isEqualTo(2);
	}
}
