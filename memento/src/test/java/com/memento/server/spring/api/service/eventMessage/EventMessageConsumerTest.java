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

import com.memento.server.api.service.eventMessage.EventMessageConsumer;
import com.memento.server.api.service.eventMessage.EventMessagePublisher;
import com.memento.server.api.service.eventMessage.dto.MemoryNotification;
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
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationRepository;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

public class EventMessageConsumerTest extends IntegrationsTestSupport {

	@Autowired
	private EventMessageConsumer eventMessageConsumer;

	@Autowired
	private EventMessagePublisher eventMessagePublisher;

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

	@AfterEach
	public void tearDown() {
		notificationRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		associateRepository.deleteAllInBatch();
		memoryRepository.deleteAllInBatch();
		communityRepository.deleteAllInBatch();
		eventRepository.deleteAllInBatch();
	}

	@DisplayName("알림 이벤트를 받아 데이터베이스에 저장한다.")
	@Test
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

		// when
		MemoryNotification eventMessage = MemoryNotification.from(memory);
		eventMessageConsumer.handleMemoryNotification(eventMessage);

		// then
		List<Notification> all = notificationRepository.findAll();
		assertThat(all.size()).isEqualTo(2);
	}
}
