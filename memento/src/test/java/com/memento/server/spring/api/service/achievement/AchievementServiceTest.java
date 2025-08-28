package com.memento.server.spring.api.service.achievement;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.memento.server.achievement.AchievementFixtures;
import com.memento.server.api.controller.achievement.dto.SearchAchievementResponse;
import com.memento.server.api.service.achievement.AchievementService;
import com.memento.server.associate.AssociateFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.achievement.Achievement;
import com.memento.server.domain.achievement.AchievementAssociate;
import com.memento.server.domain.achievement.AchievementAssociateRepository;
import com.memento.server.domain.achievement.AchievementRepository;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.member.MemberFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

public class AchievementServiceTest extends IntegrationsTestSupport {

	@Autowired
	protected AchievementService achievementService;

	@Autowired
	protected CommunityRepository communityRepository;

	@Autowired
	protected MemberRepository memberRepository;

	@Autowired
	protected AssociateRepository associateRepository;

	@Autowired
	protected AchievementRepository achievementRepository;
	@Autowired
	private AchievementAssociateRepository achievementAssociateRepository;

	@AfterEach
	void afterEach() {
		achievementAssociateRepository.deleteAll();
		achievementRepository.deleteAll();
		associateRepository.deleteAll();
		communityRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	@DisplayName("업적 조회")
	void searchTest() {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Achievement achievement1 = AchievementFixtures.achievement();
		Achievement achievement2 = AchievementFixtures.achievement();
		achievementRepository.saveAll(List.of(achievement1,achievement2));

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		// when
		SearchAchievementResponse response = achievementService.search(community.getId(), associate.getId());

		// then
		assertThat(response.achievements()).hasSize(2);
		assertThat(response.achievements().get(0).getName()).isEqualTo("achievement");
		assertThat(response.achievements().get(1).isObtained()).isFalse();
	}

	@Test
	@DisplayName("업적을 일부만 달성한 경우 조회 성공")
	void searchAchievements_partialObtained() {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Achievement achievement1 = AchievementFixtures.achievement();
		Achievement achievement2 = AchievementFixtures.achievement();
		achievementRepository.saveAll(List.of(achievement1,achievement2));

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);


		AchievementAssociate aa = achievementAssociateRepository.save(
			AchievementAssociate.builder()
				.achievement(achievement1)
				.associate(associate)
				.build()
		);

		// when
		SearchAchievementResponse response = achievementService.search(community.getId(), associate.getId());

		// then
		assertThat(response.achievements().get(0).isObtained()).isTrue();
		assertThat(response.achievements().get(1).isObtained()).isFalse();
	}
}
