package com.memento.server.spring.domain.achievement;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.memento.server.achievement.AchievementFixtures;
import com.memento.server.api.service.achievement.dto.response.SearchAchievementResponse;
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

public class AchievementRepositoryTest extends IntegrationsTestSupport {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	CommunityRepository communityRepository;

	@Autowired
	AssociateRepository associateRepository;

	@Autowired
	AchievementRepository achievementRepository;

	@Autowired
	AchievementAssociateRepository achievementAssociateRepository;

	@Test
	@DisplayName("업적 및 업적 달성 여부 조회")
	void searchTest(){
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Achievement achievement1 = AchievementFixtures.achievement();
		Achievement achievement2 = AchievementFixtures.achievement();

		achievementRepository.save(achievement2);
		achievementRepository.save(achievement1);

		AchievementAssociate achievementAssociate = AchievementAssociate.builder()
			.associate(associate)
			.achievement(achievement1)
			.build();
		achievementAssociateRepository.save(achievementAssociate);

		// when
		List<SearchAchievementResponse.Achievement> list = achievementRepository.findAllWithObtainedRecord(associate.getId());

		// then
		assertThat(list.get(0).getId()).isEqualTo(achievement2.getId());
		assertThat(list.get(0).isObtained()).isFalse();
		assertThat(list.get(1).getId()).isEqualTo(achievement1.getId());
		assertThat(list.get(1).isObtained()).isTrue();
	}
}
