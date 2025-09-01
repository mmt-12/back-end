package com.memento.server.spring.domain.profileImage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.profileImage.ProfileImage;
import com.memento.server.domain.profileImage.ProfileImageRepository;
import com.memento.server.member.MemberFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

public class ProfileImageRepositoryTest extends IntegrationsTestSupport {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private ProfileImageRepository profileImageRepository;

	@Test
	@DisplayName("프로필 이미지 조회")
	void searchTest(){
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Associate registrant = AssociateFixtures.associate(member, community);
		associateRepository.save(registrant);

		ProfileImage profileImage1 = ProfileImage.builder()
			.registrant(registrant)
			.associate(associate)
			.url("www.example.com/profileImage/test1.png")
			.build();
		profileImageRepository.save(profileImage1);

		ProfileImage profileImage2 = ProfileImage.builder()
			.registrant(registrant)
			.associate(associate)
			.url("www.example.com/profileImage/test2.png")
			.build();
		profileImageRepository.save(profileImage2);

		Pageable pageable = PageRequest.of(0,1);

		// when
		List<ProfileImage> profileImages = profileImageRepository.findProfileImageByAssociateId(associate.getId(), null, pageable);

		// then
		assertThat(profileImages.size()).isEqualTo(1);
		assertThat(profileImages.get(0).getUrl()).isEqualTo(profileImage2.getUrl());
		assertThat(profileImages.get(0).getRegistrant().getId()).isEqualTo(profileImage2.getRegistrant().getId());
	}
}
