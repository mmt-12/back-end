package com.memento.server.spring.domain.fcm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.fcm.FCMToken;
import com.memento.server.domain.fcm.FCMTokenRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.fcm.FCMTokenFixtures;
import com.memento.server.member.MemberFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

@Transactional
public class FCMTokenRepositoryTest extends IntegrationsTestSupport {

	@Autowired
	private FCMTokenRepository fcmTokenRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Test
	@DisplayName("Associate ID 목록으로 FCM 토큰을 조회한다.")
	void findAllByAssociateIds() {
		// given
		Fixtures fixtures = createFixtures();
		Associate associate1 = fixtures.associate1;
		Associate associate2 = fixtures.associate2;

		FCMToken fcmToken1 = FCMTokenFixtures.fcmToken("token1", associate1);
		FCMToken fcmToken2 = FCMTokenFixtures.fcmToken("token2", associate2);
		fcmTokenRepository.save(fcmToken1);
		fcmTokenRepository.save(fcmToken2);

		// when
		List<FCMToken> foundTokens = fcmTokenRepository.findAllByAssociateIds(
			List.of(associate1.getId(), associate2.getId()));

		// then
		assertThat(foundTokens).hasSize(2);
		assertThat(foundTokens).extracting("token")
			.containsExactlyInAnyOrder("token1", "token2");
	}

	@Test
	@DisplayName("토큰 문자열로 FCM 토큰을 삭제한다.")
	void deleteByToken() {
		// given
		Fixtures fixtures = createFixtures();
		FCMToken fcmToken = FCMTokenFixtures.fcmToken("token_to_delete", fixtures.associate1);
		FCMToken savedToken = fcmTokenRepository.save(fcmToken);

		// when
		fcmTokenRepository.deleteByToken("token_to_delete");
		fcmTokenRepository.flush();

		// then
		List<FCMToken> remainingTokens = fcmTokenRepository.findAll();
		assertThat(remainingTokens).doesNotContain(savedToken);
	}

	private record Fixtures(
		Member member1,
		Member member2,
		Community community,
		Associate associate1,
		Associate associate2
	) {
	}

	private Fixtures createFixtures() {
		Member member1 = MemberFixtures.member();
		Member member2 = MemberFixtures.member();
		Community community = CommunityFixtures.community(member1);
		Associate associate1 = AssociateFixtures.associate(member1, community);
		Associate associate2 = AssociateFixtures.associate(member2, community);

		Member savedMember1 = memberRepository.save(member1);
		Member savedMember2 = memberRepository.save(member2);
		Community savedCommunity = communityRepository.save(community);
		Associate savedAssociate1 = associateRepository.save(associate1);
		Associate savedAssociate2 = associateRepository.save(associate2);

		return new Fixtures(savedMember1, savedMember2, savedCommunity, savedAssociate1, savedAssociate2);
	}
}