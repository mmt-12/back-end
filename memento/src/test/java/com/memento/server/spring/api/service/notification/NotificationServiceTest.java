package com.memento.server.spring.api.service.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.memento.server.api.service.notification.NotificationService;
import com.memento.server.api.service.notification.dto.request.NotificationListQueryRequest;
import com.memento.server.api.service.notification.dto.response.NotificationListResponse;
import com.memento.server.api.service.notification.dto.response.NotificationResponse;
import com.memento.server.api.service.notification.dto.response.NotificationUnreadResponse;
import com.memento.server.associate.AssociateFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationRepository;
import com.memento.server.member.MemberFixtures;
import com.memento.server.notification.NotificationFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

class NotificationServiceTest extends IntegrationsTestSupport {

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@AfterEach
	void tearDown() {
		notificationRepository.deleteAllInBatch();
		associateRepository.deleteAllInBatch();
		communityRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("알림 목록을 커서 기반 페이지네이션으로 조회한다")
	void getNotifications() {
		// given
		Fixtures fixtures = createFixtures();

		Notification notification1 = NotificationFixtures.notification("title1", false, fixtures.associate);
		Notification notification2 = NotificationFixtures.notification("title2", true, fixtures.associate);
		Notification notification3 = NotificationFixtures.notification("title3", false, fixtures.associate);

		notificationRepository.saveAll(List.of(notification1, notification2, notification3));

		NotificationListQueryRequest request = NotificationListQueryRequest.of(fixtures.associate.getId(), null, 2);

		// when
		NotificationListResponse response = notificationService.getNotifications(request);

		// then
		assertThat(response.notifications()).hasSize(2);
		assertThat(response.hasNext()).isTrue();
		assertThat(response.nextCursor()).isNotNull();

		List<NotificationResponse> notifications = response.notifications();
		assertThat(notifications.get(0).title()).isEqualTo("title3");
		assertThat(notifications.get(1).title()).isEqualTo("title2");
	}

	@Test
	@DisplayName("커서가 주어진 경우 해당 커서 이후의 알림을 조회한다")
	void getNotificationsWithCursor() {
		// given
		Fixtures fixtures = createFixtures();

		Notification notification1 = NotificationFixtures.notification("title1", false, fixtures.associate);
		Notification notification2 = NotificationFixtures.notification("title2", true, fixtures.associate);
		Notification notification3 = NotificationFixtures.notification("title3", false, fixtures.associate);

		Notification savedNotification1 = notificationRepository.save(notification1);
		Notification savedNotification2 = notificationRepository.save(notification2);
		Notification savedNotification3 = notificationRepository.save(notification3);

		NotificationListQueryRequest request = NotificationListQueryRequest.of(
			fixtures.associate.getId(), savedNotification2.getId(), 10);

		// when
		NotificationListResponse response = notificationService.getNotifications(request);

		// then
		assertThat(response.notifications()).hasSize(1);
		assertThat(response.hasNext()).isFalse();
		assertThat(response.notifications().get(0).title()).isEqualTo(notification1.getTitle());
	}

	@Test
	@DisplayName("알림이 없을 때 빈 리스트를 반환한다")
	void getNotificationsFromEmptyAssociate() {
		// given
		Fixtures fixtures = createFixtures();
		NotificationListQueryRequest request = NotificationListQueryRequest.of(fixtures.associate.getId(), null, 10);

		// when
		NotificationListResponse response = notificationService.getNotifications(request);

		// then
		assertThat(response.notifications()).isEmpty();
		assertThat(response.hasNext()).isFalse();
		assertThat(response.nextCursor()).isNull();
	}

	@Test
	@DisplayName("읽지 않은 알림 개수를 조회한다")
	void getUnread() {
		// given
		Fixtures fixtures = createFixtures();

		Notification unreadNotification1 = NotificationFixtures.unreadNotification(fixtures.associate);
		Notification readNotification = NotificationFixtures.readNotification(fixtures.associate);
		Notification unreadNotification2 = NotificationFixtures.unreadNotification(fixtures.associate);

		notificationRepository.saveAll(List.of(unreadNotification1, readNotification, unreadNotification2));

		// when
		NotificationUnreadResponse response = notificationService.getUnread(fixtures.associate.getId());

		// then
		assertThat(response.hasUnread()).isTrue();
		assertThat(response.count()).isEqualTo(2);
	}

	@Test
	@DisplayName("읽지 않은 알림이 없을 때 hasUnread는 false이다")
	void getUnreadWhenAllRead() {
		// given
		Fixtures fixtures = createFixtures();

		Notification readNotification1 = NotificationFixtures.readNotification(fixtures.associate);
		Notification readNotification2 = NotificationFixtures.readNotification(fixtures.associate);

		notificationRepository.saveAll(List.of(readNotification1, readNotification2));

		// when
		NotificationUnreadResponse response = notificationService.getUnread(fixtures.associate.getId());

		// then
		assertThat(response.hasUnread()).isFalse();
		assertThat(response.count()).isEqualTo(0);
	}

	@Test
	@DisplayName("알림이 없을 때 읽지 않은 알림 개수는 0이다")
	void getUnreadFromEmptyAssociate() {
		// given
		Fixtures fixtures = createFixtures();

		// when
		NotificationUnreadResponse response = notificationService.getUnread(fixtures.associate.getId());

		// then
		assertThat(response.hasUnread()).isFalse();
		assertThat(response.count()).isEqualTo(0);
	}

	private record Fixtures(
		Member member,
		Community community,
		Associate associate
	) {

	}

	private Fixtures createFixtures() {
		Member member = MemberFixtures.member();
		Community community = CommunityFixtures.community(member);
		Associate associate = AssociateFixtures.associate(member, community);

		Member savedMember = memberRepository.save(member);
		Community savedCommunity = communityRepository.save(community);
		Associate savedAssociate = associateRepository.save(associate);

		return new Fixtures(savedMember, savedCommunity, savedAssociate);
	}
}
