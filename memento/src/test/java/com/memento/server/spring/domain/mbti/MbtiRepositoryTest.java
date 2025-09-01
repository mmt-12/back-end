package com.memento.server.spring.domain.mbti;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.memento.server.api.service.mbti.dto.MbtiSearchDto;
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

public class MbtiRepositoryTest extends IntegrationsTestSupport {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	CommunityRepository communityRepository;

	@Autowired
	AssociateRepository associateRepository;

	@Autowired
	MbtiTestRepository mbtiTestRepository;

	@Test
	@DisplayName("mbti 단건 조회")
	void searchTest(){
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate from = AssociateFixtures.associate(member, community);
		associateRepository.save(from);

		Associate to = AssociateFixtures.associate(member, community);
		associateRepository.save(to);

		MbtiTest mbti = MbtiTest.builder()
			.toAssociate(to)
			.fromAssociate(from)
			.mbti(Mbti.ENFP)
			.build();
		mbtiTestRepository.save(mbti);

		// when
		MbtiTest mbtiTest = mbtiTestRepository.findByFromAssociateIdAndToAssociateId(from.getId(), to.getId());

		// then
		assertThat(mbtiTest.getMbti()).isEqualTo(Mbti.ENFP);
	}

	@Test
	@DisplayName("mbti 결과 조회")
	void searchResultTest(){
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate from = AssociateFixtures.associate(member, community);
		associateRepository.save(from);

		Associate to = AssociateFixtures.associate(member, community);
		associateRepository.save(to);

		MbtiTest mbti1 = MbtiTest.builder()
			.toAssociate(to)
			.fromAssociate(from)
			.mbti(Mbti.ENFP)
			.build();
		MbtiTest mbti2 = MbtiTest.builder()
			.toAssociate(to)
			.fromAssociate(from)
			.mbti(Mbti.ENFJ)
			.build();
		MbtiTest mbti3 = MbtiTest.builder()
			.toAssociate(to)
			.fromAssociate(from)
			.mbti(Mbti.ENFJ)
			.build();
		MbtiTest mbti4 = MbtiTest.builder()
			.toAssociate(to)
			.fromAssociate(from)
			.mbti(Mbti.ISTJ)
			.build();
		MbtiTest mbti5 = MbtiTest.builder()
			.toAssociate(to)
			.fromAssociate(from)
			.mbti(Mbti.INFJ)
			.build();
		mbtiTestRepository.save(mbti1);
		mbtiTestRepository.save(mbti2);
		mbtiTestRepository.save(mbti3);
		mbtiTestRepository.save(mbti4);
		mbtiTestRepository.save(mbti5);

		// when
		MbtiSearchDto result = mbtiTestRepository.countMbtiByToAssociate(to.getId());

		// then
		assertThat(result.ENFP()).isEqualTo(1L);
		assertThat(result.ENFJ()).isEqualTo(2L);
		assertThat(result.ISTJ()).isEqualTo(1L);
		assertThat(result.INFJ()).isEqualTo(1L);
	}
}
