package com.memento.server.spring.api.service.mbti;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.memento.server.api.service.mbti.dto.response.SearchMbtiResponse;
import com.memento.server.api.service.achievement.AchievementEventPublisher;
import com.memento.server.api.service.fcm.FCMEventPublisher;
import com.memento.server.api.service.mbti.MbtiService;
import com.memento.server.associate.AssociateFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.mbti.Mbti;
import com.memento.server.domain.mbti.MbtiTest;
import com.memento.server.domain.mbti.MbtiTestRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.member.MemberFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

public class MbtiServiceTest extends IntegrationsTestSupport {

	@Autowired
	private MbtiService mbtiService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private MbtiTestRepository mbtiTestRepository;

	@MockitoBean
	private FCMEventPublisher fcmEventPublisher;

	@MockitoBean
	private AchievementEventPublisher achievementEventPublisher;

	@AfterEach
	void afterEach(){
		mbtiTestRepository.deleteAll();
		associateRepository.deleteAll();
		communityRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	@DisplayName("mbti 등록")
	void createTest(){
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate1 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate1);

		Associate associate2 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate2);

		//when
		mbtiService.create(community.getId(), associate1.getId(), associate2.getId(), Mbti.INFP);

		//then
		MbtiTest mbti = mbtiTestRepository.findByFromAssociateIdAndToAssociateId(associate1.getId(), associate2.getId());

		assertThat(mbti.getMbti()).isEqualTo(Mbti.INFP);
	}

	@Test
	@DisplayName("mbti 수정")
	void updateTest(){
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate1 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate1);

		Associate associate2 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate2);

		MbtiTest mbtiTest = MbtiTest.builder()
			.mbti(Mbti.ENFP)
			.fromAssociate(associate1)
			.toAssociate(associate2)
			.build();
		mbtiTestRepository.save(mbtiTest);

		//when
		mbtiService.create(community.getId(), associate1.getId(), associate2.getId(), Mbti.INFP);

		//then
		MbtiTest mbti = mbtiTestRepository.findByFromAssociateIdAndToAssociateId(associate1.getId(), associate2.getId());

		assertThat(mbti.getMbti()).isEqualTo(Mbti.INFP);
	}

	@Test
	@DisplayName("mbti 조회")
	void searchTest(){
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate1 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate1);

		Associate associate2 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate2);

		MbtiTest mbtiTest1 = MbtiTest.builder()
			.mbti(Mbti.ENFJ)
			.toAssociate(associate1)
			.fromAssociate(associate2)
			.build();
		mbtiTestRepository.save(mbtiTest1);

		MbtiTest mbtiTest2 = MbtiTest.builder()
			.mbti(Mbti.ENFP)
			.toAssociate(associate1)
			.fromAssociate(associate2)
			.build();
		mbtiTestRepository.save(mbtiTest2);

		//when
		SearchMbtiResponse response = mbtiService.search(community.getId(), associate1.getId());

		//then
		assertThat(response.ENFP()).isEqualTo(1L);
		assertThat(response.ENFJ()).isEqualTo(1L);
		assertThat(response.ENTJ()).isEqualTo(0);
	}
}
