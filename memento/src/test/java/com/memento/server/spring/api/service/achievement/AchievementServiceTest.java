package com.memento.server.spring.api.service.achievement;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.achievement.dto.SearchAchievementResponse;
import com.memento.server.api.service.achievement.AchievementService;
import com.memento.server.api.service.achievement.dto.SearchAchievementDto;
import com.memento.server.domain.achievement.Achievement;
import com.memento.server.domain.achievement.AchievementRepository;
import com.memento.server.domain.achievement.AchievementType;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;

@SpringBootTest
@Transactional
public class AchievementServiceTest{

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

	@Test
	@DisplayName("업적 조회")
	void searchTest() {
		// given
		Member member = Member.builder()
			.name("example")
			.email("example@example.com")
			.kakaoId(123L)
			.birthday(LocalDate.of(1999, 1, 1))
			.build();
		memberRepository.save(member);

		Community community = Community.builder()
			.member(member)
			.name("example")
			.build();
		communityRepository.save(community);

		Achievement achievement1 = Achievement.builder()
			.name("Novice")
			.criteria("example")
			.type(AchievementType.OPEN)
			.build();
		Achievement achievement2 = Achievement.builder()
			.name("Expert")
			.criteria("example")
			.type(AchievementType.OPEN)
			.build();
		achievementRepository.save(achievement1);
		achievementRepository.save(achievement2);

		Associate associate = Associate.builder()
			.community(community)
			.member(member)
			.nickname("example")
			.build();
		associateRepository.save(associate);

		// when
		SearchAchievementResponse response = achievementService.search(community.getId(), associate.getId());

		// then
		assertThat(response.achievements()).hasSize(2);
		assertThat(response.achievements().get(0).getName()).isEqualTo("Novice");
		assertThat(response.achievements().get(1).isObtained()).isFalse();
	}
}
