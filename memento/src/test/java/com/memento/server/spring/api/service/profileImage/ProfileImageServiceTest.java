package com.memento.server.spring.api.service.profileImage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.controller.profileImage.dto.SearchProfileImageResponse;
import com.memento.server.api.service.profileImage.ProfileImageService;
import com.memento.server.associate.AssociateFixtures;
import com.memento.server.common.exception.MementoException;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.config.MinioProperties;
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

public class ProfileImageServiceTest extends IntegrationsTestSupport {

	@Autowired
	private ProfileImageService profileImageService;

	@Autowired
	private ProfileImageRepository profileImageRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@AfterEach
	void afterEach(){
		profileImageRepository.deleteAll();
		associateRepository.deleteAll();
		communityRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	@DisplayName("프로필 이미지 생성")
	void createTest(){
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Associate registrant = AssociateFixtures.associate(member, community);
		associateRepository.save(registrant);

		MultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
		String url = "https://example.com/test.png";
		given(minioService.createFile(file, MinioProperties.FileType.PROFILE_IMAGE))
			.willReturn(url);

		// when
		profileImageService.create(community.getId(), associate.getId(),registrant.getId(), file);

		//then
		List<ProfileImage> profileImage = profileImageRepository.findAllByAssociateId(associate.getId());

		assertThat(profileImage.getFirst().getUrl()).isEqualTo(url);
	}

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
			.url("test1.png")
			.associate(associate)
			.registrant(registrant)
			.build();
		profileImageRepository.save(profileImage1);

		ProfileImage profileImage2 = ProfileImage.builder()
			.url("test2.png")
			.associate(associate)
			.registrant(registrant)
			.build();
		profileImageRepository.save(profileImage2);

		Long cursor = null;

		// when
		SearchProfileImageResponse response = profileImageService.search(community.getId(), associate.getId(), 10, cursor);

		//then
		assertThat(response.profileImages().size()).isEqualTo(2);
		assertThat(response.profileImages().getFirst().getUrl()).isEqualTo("test2.png");
		assertThat(response.profileImages().getFirst().isRegister()).isFalse();
		assertThat(response.hasNext()).isFalse();
		assertThat(response.nextCursor()).isNull();
	}

	@Test
	@DisplayName("프로필 이미지 조회 pagination")
	void searchWithPaginationTest(){
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
			.url("test1.png")
			.associate(associate)
			.registrant(registrant)
			.build();
		profileImageRepository.save(profileImage1);

		ProfileImage profileImage2 = ProfileImage.builder()
			.url("test2.png")
			.associate(associate)
			.registrant(registrant)
			.build();
		profileImageRepository.save(profileImage2);

		Long cursor = null;

		// when
		SearchProfileImageResponse response = profileImageService.search(community.getId(), associate.getId(), 1, cursor);

		//then
		assertThat(response.nextCursor()).isEqualTo(profileImage1.getId());
		assertThat(response.hasNext()).isTrue();
	}

	@Test
	@DisplayName("프로필 이미지 삭제")
	void deleteTest(){
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Associate registrant = AssociateFixtures.associate(member, community);
		associateRepository.save(registrant);

		ProfileImage profileImage = ProfileImage.builder()
			.url("test1.png")
			.associate(associate)
			.registrant(registrant)
			.build();
		profileImageRepository.save(profileImage);

		// when
		profileImageService.delete(community.getId(), registrant.getId(), associate.getId(), profileImage.getId());

		//then
		ProfileImage response = profileImageRepository.findById(profileImage.getId())
			.orElseThrow();

		assertThat(response.getDeletedAt()).isNotNull();
	}

	@Test
	@DisplayName("존재하지 않는 프로필 이미지는 삭제할 수 없다")
	void deleteWithNotExistProfileImageTest(){
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Associate registrant = AssociateFixtures.associate(member, community);
		associateRepository.save(registrant);

		// when & then
		assertThrows(MementoException.class, () -> profileImageService.delete(community.getId(), registrant.getId(), associate.getId(), 1L));
	}

	@Test
	@DisplayName("삭제 권한이 없는 참여자는 삭제할 수 없다")
	void deleteWithNotAuthorityTest(){
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Associate registrant = AssociateFixtures.associate(member, community);
		associateRepository.save(registrant);

		Associate notAuthorityAssociate = AssociateFixtures.associate(member, community);
		associateRepository.save(notAuthorityAssociate);

		ProfileImage profileImage = ProfileImage.builder()
			.url("test1.png")
			.associate(associate)
			.registrant(registrant)
			.build();
		profileImageRepository.save(profileImage);

		// when & then
		assertThrows(MementoException.class, () -> profileImageService.delete(community.getId(), notAuthorityAssociate.getId(), associate.getId(), profileImage.getId()));
	}


}
