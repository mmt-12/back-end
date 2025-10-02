package com.memento.server.spring.domain.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.service.notification.dto.request.NotificationListQueryRequest;
import com.memento.server.api.service.notification.dto.response.NotificationResponse;
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

import jakarta.persistence.EntityManager;

@Transactional
class NotificationRepositoryTest extends IntegrationsTestSupport {

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("Associate ID로 커서 기반 페이지네이션을 사용하여 알림을 조회한다")
	void findNotificationsByAssociateWithCursor() {
		// given
		Fixtures fixtures = createFixtures();

		Notification notification1 = NotificationFixtures.notification("title1", false, fixtures.associate);
		Notification notification2 = NotificationFixtures.notification("title2", true, fixtures.associate);
		Notification notification3 = NotificationFixtures.notification("title3", false, fixtures.associate);

		Notification savedNotification1 = notificationRepository.save(notification1);
		Notification savedNotification2 = notificationRepository.save(notification2);
		Notification savedNotification3 = notificationRepository.save(notification3);

		NotificationListQueryRequest request = NotificationListQueryRequest.of(fixtures.associate.getId(), null, 10);

		// when
		List<Notification> notifications = notificationRepository.findNotificationsByAssociateWithCursor(request);

		// then
		assertThat(notifications).hasSize(3);
		assertThat(notifications.get(0).getTitle()).isEqualTo(savedNotification3.getTitle());
		assertThat(notifications.get(1).getTitle()).isEqualTo(savedNotification2.getTitle());
		assertThat(notifications.get(2).getTitle()).isEqualTo(savedNotification1.getTitle());
		assertThat(notifications.get(0).getIsRead()).isFalse();
		assertThat(notifications.get(1).getIsRead()).isTrue();
		assertThat(notifications.get(2).getIsRead()).isFalse();
	}

	@Test
	@DisplayName("커서가 주어진 경우 해당 ID보다 작은 알림을 조회한다")
	void findNotificationsByAssociateWithCursorFilter() {
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
		List<Notification> notifications = notificationRepository.findNotificationsByAssociateWithCursor(request);

		// then
		assertThat(notifications).hasSize(1);
		assertThat(notifications.getFirst().getTitle()).isEqualTo(savedNotification1.getTitle());
		assertThat(notifications.getFirst().getId()).isEqualTo(savedNotification1.getId());
	}

	@Test
	@DisplayName("삭제된 알림은 조회되지 않는다")
	void findNotificationsByAssociateExcludeDeleted() {
		// given
		Fixtures fixtures = createFixtures();

		Notification notification1 = NotificationFixtures.notification("title1", false, fixtures.associate);
		Notification notification2 = NotificationFixtures.notification("title2", true, fixtures.associate);

		Notification savedNotification1 = notificationRepository.save(notification1);
		Notification savedNotification2 = notificationRepository.save(notification2);

		savedNotification2.markDeleted();
		em.flush();

		NotificationListQueryRequest request = NotificationListQueryRequest.of(fixtures.associate.getId(), null, 10);

		// when
		List<Notification> notifications = notificationRepository.findNotificationsByAssociateWithCursor(request);

		// then
		assertThat(notifications).hasSize(1);
		assertThat(notifications.getFirst().getTitle()).isEqualTo(savedNotification1.getTitle());
		assertThat(notifications.getFirst().getId()).isEqualTo(savedNotification1.getId());
	}

	@Test
	@DisplayName("알림이 없을 때 빈 리스트를 반환한다")
	void findNotificationsByAssociateWithNoNotifications() {
		// given
		Fixtures fixtures = createFixtures();
		NotificationListQueryRequest request = NotificationListQueryRequest.of(fixtures.associate.getId(), null, 10);

		// when
		List<Notification> notifications = notificationRepository.findNotificationsByAssociateWithCursor(request);

		// then
		assertThat(notifications).isEmpty();
	}

	@Test
	@DisplayName("Associate ID로 읽지 않은 알림 개수를 조회한다")
	void countUnreadNotificationsByAssociate() {
		// given
		Fixtures fixtures = createFixtures();

		Notification unreadNotification1 = NotificationFixtures.unreadNotification(fixtures.associate);
		Notification readNotification = NotificationFixtures.readNotification(fixtures.associate);
		Notification unreadNotification2 = NotificationFixtures.unreadNotification(fixtures.associate);

		notificationRepository.saveAll(List.of(unreadNotification1, readNotification, unreadNotification2));

		// when
		int count = notificationRepository.countUnreadNotificationsByAssociate(fixtures.associate.getId());

		// then
		assertThat(count).isEqualTo(2);
	}

	@Test
	@DisplayName("읽지 않은 알림이 없을 때 0을 반환한다")
	void countUnreadNotificationsByAssociateWhenAllRead() {
		// given
		Fixtures fixtures = createFixtures();

		Notification readNotification1 = NotificationFixtures.readNotification(fixtures.associate);
		Notification readNotification2 = NotificationFixtures.readNotification(fixtures.associate);

		notificationRepository.saveAll(List.of(readNotification1, readNotification2));

		// when
		int count = notificationRepository.countUnreadNotificationsByAssociate(fixtures.associate.getId());

		// then
		assertThat(count).isEqualTo(0);
	}

	@Test
	@DisplayName("삭제된 알림은 읽지 않은 알림 개수에서 제외된다")
	void countUnreadNotificationsByAssociateExcludeDeleted() {
		// given
		Fixtures fixtures = createFixtures();

		Notification unreadNotification = NotificationFixtures.unreadNotification(fixtures.associate);
		Notification deletedUnreadNotification = NotificationFixtures.unreadNotification(fixtures.associate);

		Notification savedUnreadNotification = notificationRepository.save(unreadNotification);
		Notification savedDeletedNotification = notificationRepository.save(deletedUnreadNotification);

		savedDeletedNotification.markDeleted();
		em.flush();

		// when
		int count = notificationRepository.countUnreadNotificationsByAssociate(fixtures.associate.getId());

		// then
		assertThat(count).isEqualTo(1);
	}

	@Test
	@DisplayName("다른 Associate의 알림은 조회되지 않는다")
	void findNotificationsByAssociateOnlyOwnNotifications() {
		// given
		Fixtures fixtures1 = createFixtures();
		Fixtures fixtures2 = createFixtures();

		Notification notification1 = NotificationFixtures.notification("associate1 notification", false, fixtures1.associate);
		Notification notification2 = NotificationFixtures.notification("associate2 notification", false, fixtures2.associate);

		notificationRepository.saveAll(List.of(notification1, notification2));

		NotificationListQueryRequest request = NotificationListQueryRequest.of(fixtures1.associate.getId(), null, 10);

		// when
		List<Notification> notifications = notificationRepository.findNotificationsByAssociateWithCursor(request);

		// then
		assertThat(notifications).hasSize(1);
		assertThat(notifications.getFirst().getTitle()).isEqualTo(notification1.getTitle());
	}

	@Test
	@DisplayName("알림이 없을 때 읽지 않은 알림 개수는 0을 반환한다")
	void countUnreadNotificationsByAssociateWithNoNotifications() {
		// given
		Fixtures fixtures = createFixtures();

		// when
		int count = notificationRepository.countUnreadNotificationsByAssociate(fixtures.associate.getId());

		// then
		assertThat(count).isEqualTo(0);
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
