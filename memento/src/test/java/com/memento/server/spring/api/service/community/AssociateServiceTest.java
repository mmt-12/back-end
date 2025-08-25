package com.memento.server.spring.api.service.community;

import static com.memento.server.common.error.ErrorCodes.COMMUNITY_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.community.dto.AssociateListResponse;
import com.memento.server.api.controller.community.dto.SearchAssociateResponse;
import com.memento.server.api.service.community.AssociateService;
import com.memento.server.common.exception.MementoException;
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
public class AssociateServiceTest{

	@Autowired
	protected AssociateService associateService;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	protected AchievementRepository achievementRepository;

	@Autowired
	protected AssociateRepository associateRepository;

	@Test
	@DisplayName("커뮤니티 참여자 목록 조회")
	void searchAll() {
		// given
		Member member1 = memberRepository.save(Member.create("김가가", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(Member.create("김나나", "muge@test.com", LocalDate.of(1990, 1, 1), 1002L));
		Member member3 = memberRepository.save(Member.create("김다다", "muge@test.com", LocalDate.of(1990, 1, 1), 1003L));
		Member member4 = memberRepository.save(Member.create("김라라", "muge@test.com", LocalDate.of(1990, 1, 1), 1004L));
		Member member5 = memberRepository.save(Member.create("김마마", "muge@test.com", LocalDate.of(1990, 1, 1), 1005L));
		Member member6 = memberRepository.save(Member.create("김바바", "muge@test.com", LocalDate.of(1990, 1, 1), 1006L));
		Member member7 = memberRepository.save(Member.create("김사사", "muge@test.com", LocalDate.of(1990, 1, 1), 1007L));
		Member member8 = memberRepository.save(Member.create("김아아", "muge@test.com", LocalDate.of(1990, 1, 1), 1008L));
		Member member9 = memberRepository.save(Member.create("김자자", "muge@test.com", LocalDate.of(1990, 1, 1), 1009L));
		Member member10 = memberRepository.save(Member.create("김차차", "muge@test.com", LocalDate.of(1990, 1, 1), 1010L));
		Member member11 = memberRepository.save(Member.create("김카카", "muge@test.com", LocalDate.of(1990, 1, 1), 1011L));
		Community community = communityRepository.save(Community.create("comm", member1));
		Associate associate1 = associateRepository.save(Associate.create("가가", member1, community));
		Associate associate2 = associateRepository.save(Associate.create("나나", member2, community));
		Associate associate3 = associateRepository.save(Associate.create("다다", member3, community));
		Associate associate4 = associateRepository.save(Associate.create("라라", member4, community));
		Associate associate5 = associateRepository.save(Associate.create("마마", member5, community));
		Associate associate6 = associateRepository.save(Associate.create("바바", member6, community));
		Associate associate7 = associateRepository.save(Associate.create("사사", member7, community));
		Associate associate8 = associateRepository.save(Associate.create("아아", member8, community));
		Associate associate9 = associateRepository.save(Associate.create("자자", member9, community));
		Associate associate10 = associateRepository.save(Associate.create("차차", member10, community));
		Associate associate11 = associateRepository.save(Associate.create("카카", member11, community));

		// when
		AssociateListResponse associateListResponse = associateService.searchAll(community.getId(), "", null, 10);

		// then
		assertThat(associateListResponse.communityName()).isEqualTo(community.getName());
		assertThat(associateListResponse.associates().size()).isEqualTo(10);
		assertThat(associateListResponse.associates().getFirst().nickname()).isEqualTo(associate11.getNickname());
		assertThat(associateListResponse.associates().getLast().nickname()).isEqualTo(associate2.getNickname());
		assertThat(associateListResponse.cursor()).isEqualTo(associate2.getId());
		assertThat(associateListResponse.hasNext()).isEqualTo(true);
	}

	@Test
	@DisplayName("키워드로 커뮤니티 참여자 목록을 조회한다.")
	void searchAll_withKeyword() {
		// given
		Member member1 = memberRepository.save(Member.create("홍길동", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(Member.create("아무개", "muge@test.com", LocalDate.of(1990, 1, 1), 1002L));
		Community community = communityRepository.save(Community.create("comm", member1));
		Associate associate1 = associateRepository.save(Associate.create("홍홍홍", member1, community));
		Associate associate2 = associateRepository.save(Associate.create("아아아", member2, community));

		// when
		AssociateListResponse associateListResponse = associateService.searchAll(community.getId(), "홍홍", null, 10);

		// then
		assertThat(associateListResponse.communityName()).isEqualTo(community.getName());
		assertThat(associateListResponse.associates().size()).isEqualTo(1);
		assertThat(associateListResponse.associates().getFirst().nickname()).isEqualTo(associate1.getNickname());
		assertThat(associateListResponse.cursor()).isEqualTo(associate1.getId());
		assertThat(associateListResponse.hasNext()).isEqualTo(false);
	}

	@Test
	@DisplayName("커뮤니티 참여자 목록을 커서 방식으로 조회한다.")
	void searchAll_withCursor() {
		// given
		Member member1 = memberRepository.save(Member.create("김가가", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(Member.create("김나나", "muge@test.com", LocalDate.of(1990, 1, 1), 1002L));
		Member member3 = memberRepository.save(Member.create("김다다", "muge@test.com", LocalDate.of(1990, 1, 1), 1003L));
		Member member4 = memberRepository.save(Member.create("김라라", "muge@test.com", LocalDate.of(1990, 1, 1), 1004L));
		Member member5 = memberRepository.save(Member.create("김마마", "muge@test.com", LocalDate.of(1990, 1, 1), 1005L));
		Member member6 = memberRepository.save(Member.create("김바바", "muge@test.com", LocalDate.of(1990, 1, 1), 1006L));
		Member member7 = memberRepository.save(Member.create("김사사", "muge@test.com", LocalDate.of(1990, 1, 1), 1007L));
		Member member8 = memberRepository.save(Member.create("김아아", "muge@test.com", LocalDate.of(1990, 1, 1), 1008L));
		Member member9 = memberRepository.save(Member.create("김자자", "muge@test.com", LocalDate.of(1990, 1, 1), 1009L));
		Member member10 = memberRepository.save(Member.create("김차차", "muge@test.com", LocalDate.of(1990, 1, 1), 1010L));
		Member member11 = memberRepository.save(Member.create("김카카", "muge@test.com", LocalDate.of(1990, 1, 1), 1011L));
		Community community = communityRepository.save(Community.create("comm", member1));
		Associate associate1 = associateRepository.save(Associate.create("가가", member1, community));
		Associate associate2 = associateRepository.save(Associate.create("나나", member2, community));
		Associate associate3 = associateRepository.save(Associate.create("다다", member3, community));
		Associate associate4 = associateRepository.save(Associate.create("라라", member4, community));
		Associate associate5 = associateRepository.save(Associate.create("마마", member5, community));
		Associate associate6 = associateRepository.save(Associate.create("바바", member6, community));
		Associate associate7 = associateRepository.save(Associate.create("사사", member7, community));
		Associate associate8 = associateRepository.save(Associate.create("아아", member8, community));
		Associate associate9 = associateRepository.save(Associate.create("자자", member9, community));
		Associate associate10 = associateRepository.save(Associate.create("차차", member10, community));
		Associate associate11 = associateRepository.save(Associate.create("카카", member11, community));

		// when
		AssociateListResponse associateListResponse = associateService.searchAll(community.getId(), "",
			associate11.getId(), 10);

		// then
		assertThat(associateListResponse.communityName()).isEqualTo(community.getName());
		assertThat(associateListResponse.associates().size()).isEqualTo(10);
		assertThat(associateListResponse.associates().getFirst().nickname()).isEqualTo(associate10.getNickname());
		assertThat(associateListResponse.associates().getLast().nickname()).isEqualTo(associate1.getNickname());
		assertThat(associateListResponse.cursor()).isEqualTo(associate1.getId());
		assertThat(associateListResponse.hasNext()).isEqualTo(false);
	}

	@Test
	@DisplayName("커뮤니티 참여자 목록을 커서 방식과 키워드로 조회한다.")
	void searchAll_WithKeywordCursor() {
		// given
		Member member1 = memberRepository.save(Member.create("김가가", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(Member.create("김나나", "muge@test.com", LocalDate.of(1990, 1, 1), 1002L));
		Member member3 = memberRepository.save(Member.create("김다다", "muge@test.com", LocalDate.of(1990, 1, 1), 1003L));
		Member member4 = memberRepository.save(Member.create("김라라", "muge@test.com", LocalDate.of(1990, 1, 1), 1004L));
		Member member5 = memberRepository.save(Member.create("김마마", "muge@test.com", LocalDate.of(1990, 1, 1), 1005L));
		Member member6 = memberRepository.save(Member.create("김바바", "muge@test.com", LocalDate.of(1990, 1, 1), 1006L));
		Member member7 = memberRepository.save(Member.create("김사사", "muge@test.com", LocalDate.of(1990, 1, 1), 1007L));
		Member member8 = memberRepository.save(Member.create("김아아", "muge@test.com", LocalDate.of(1990, 1, 1), 1008L));
		Member member9 = memberRepository.save(Member.create("김자자", "muge@test.com", LocalDate.of(1990, 1, 1), 1009L));
		Member member10 = memberRepository.save(Member.create("김차차", "muge@test.com", LocalDate.of(1990, 1, 1), 1010L));
		Member member11 = memberRepository.save(Member.create("김카카", "muge@test.com", LocalDate.of(1990, 1, 1), 1011L));
		Member member12 = memberRepository.save(
			Member.create("김가가2", "muge@test.com", LocalDate.of(1990, 1, 1), 1012L));
		Community community = communityRepository.save(Community.create("comm", member1));
		Associate associate1 = associateRepository.save(Associate.create("가가", member1, community));
		Associate associate2 = associateRepository.save(Associate.create("나나", member2, community));
		Associate associate3 = associateRepository.save(Associate.create("가가2", member3, community));
		Associate associate4 = associateRepository.save(Associate.create("가가3", member4, community));
		Associate associate5 = associateRepository.save(Associate.create("가가4", member5, community));
		Associate associate6 = associateRepository.save(Associate.create("가가5", member6, community));
		Associate associate7 = associateRepository.save(Associate.create("가가6", member7, community));
		Associate associate8 = associateRepository.save(Associate.create("가가7", member8, community));
		Associate associate9 = associateRepository.save(Associate.create("가가8", member9, community));
		Associate associate10 = associateRepository.save(Associate.create("가가9", member10, community));
		Associate associate11 = associateRepository.save(Associate.create("가가10", member11, community));
		Associate associate12 = associateRepository.save(Associate.create("가가11", member12, community));

		// when
		AssociateListResponse associateListResponse = associateService.searchAll(community.getId(), "가가",
			associate12.getId(), 10);

		// then
		assertThat(associateListResponse.communityName()).isEqualTo(community.getName());
		assertThat(associateListResponse.associates().size()).isEqualTo(10);
		assertThat(associateListResponse.associates().getFirst().nickname()).isEqualTo(associate11.getNickname());
		assertThat(associateListResponse.associates().getLast().nickname()).isEqualTo(associate1.getNickname());
		assertThat(associateListResponse.cursor()).isEqualTo(associate1.getId());
		assertThat(associateListResponse.hasNext()).isEqualTo(false);
	}

	@Test
	@DisplayName("존재하지 않는 커뮤니티 조회를 하면 COMMUNITY_NOT_FOUND 오류가 발생한다.")
	void searchAll_withNotFound_throwsException() {
		// given

		// when & then
		assertThatThrownBy(() ->
			associateService.searchAll(1L, "", null, 10)
		)
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException me = (MementoException)ex;
				assertThat(me.getErrorCode()).isEqualTo(COMMUNITY_NOT_FOUND);
			});
	}

	@Test
	@DisplayName("프로필 상세 조회")
	void searchTest() {
		// given
		Achievement achievement = Achievement.builder()
			.name("test name")
			.criteria("example")
			.type(AchievementType.OPEN)
			.build();
		achievementRepository.save(achievement);

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

		Associate associate = Associate.builder()
			.community(community)
			.nickname("test nickname")
			.achievement(achievement)
			.profileImageUrl("www.example.com")
			.introduction("test introduction")
			.member(member)
			.build();
		associateRepository.save(associate);

		// when
		SearchAssociateResponse response = associateService.search(community.getId(), associate.getId());

		// then
		assertEquals("test nickname", response.nickname());
		assertEquals("test name", response.achievement().getName());
		assertEquals("www.example.com", response.imageUrl());
		assertEquals("test introduction", response.introduction());
		assertEquals(LocalDate.of(1999, 1, 1), response.birthday());
	}

	@Test
	@DisplayName("프로필 수정")
	void updateTest() {
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

		Achievement oldAchievement = Achievement.builder()
			.name("Novice")
			.criteria("example")
			.type(AchievementType.OPEN)
			.build();
		Achievement newAchievement = Achievement.builder()
			.name("Expert")
			.criteria("example")
			.type(AchievementType.OPEN)
			.build();
		achievementRepository.save(oldAchievement);
		achievementRepository.save(newAchievement);

		Associate associate = Associate.builder()
			.community(community)
			.member(member)
			.achievement(oldAchievement)
			.nickname("example")
			.build();
		associateRepository.save(associate);

		// when
		associateService.update(
			community.getId(),
			associate.getId(),
			"new-image",
			null,
			newAchievement.getId(),
			"New Intro"
		);

		// then
		Associate updatedAssociate = associateRepository.findByIdAndDeletedAtIsNull(associate.getId())
			.orElseThrow();

		assertThat(updatedAssociate.getProfileImageUrl()).isEqualTo("new-image");
		assertThat(updatedAssociate.getNickname()).isEqualTo("example");
		assertThat(updatedAssociate.getAchievement().getId()).isEqualTo(newAchievement.getId());
		assertThat(updatedAssociate.getAchievement().getName()).isEqualTo("Expert");
		assertThat(updatedAssociate.getIntroduction()).isEqualTo("New Intro");
	}
}
