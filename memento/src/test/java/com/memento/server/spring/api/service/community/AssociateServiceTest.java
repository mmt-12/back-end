package com.memento.server.spring.api.service.community;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.community.dto.SearchAssociateResponse;
import com.memento.server.domain.achievement.Achievement;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.member.Member;
import com.memento.server.spring.api.service.ServiceTestSupport;

public class AssociateServiceTest extends ServiceTestSupport {

	@Test
	@DisplayName("프로필 상세 조회")
	void searchTest() {
		// given
		Long communityId = 1L;
		Long associateId = 2L;

		Achievement achievement = Achievement.builder()
			.id(10L)
			.name("test name")
			.build();

		Member member = Member.builder()
			.birthday(LocalDate.of(1990, 5, 1))
			.build();

		Associate associate = Associate.builder()
			.id(associateId)
			.nickname("test nickname")
			.achievement(achievement)
			.profileImageUrl("www.example.com")
			.introduction("test introduction")
			.member(member)
			.build();

		doReturn(associate)
			.when(associateService)
			.validAssociate(communityId, associateId);

		// when
		SearchAssociateResponse response = associateService.search(communityId, associateId);

		// then
		assertEquals("test nickname", response.nickname());
		assertEquals("test name", response.achievement().getName());
		assertEquals("www.example.com", response.imageUrl());
		assertEquals("test introduction", response.introduction());
		assertEquals(LocalDate.of(1990, 5, 1), response.birthday());
	}

	@Test
	@DisplayName("프로필 수정")
	void updateTest() {
		// given
		Long communityId = 1L;
		Long associateId = 2L;

		Achievement oldAchievement = Achievement.builder()
			.id(10L)
			.name("Novice")
			.build();

		Achievement newAchievement = Achievement.builder()
			.id(11L)
			.name("Expert")
			.build();

		Associate associate = spy(
			Associate.builder()
				.id(associateId)
				.nickname("OldName")
				.achievement(oldAchievement)
				.profileImageUrl("old-image")
				.introduction("Old Intro")
				.build()
		);

		doReturn(associate)
			.when(associateService)
			.validAssociate(communityId, associateId);

		when(achievementRepository.findById(11L))
			.thenReturn(Optional.of(newAchievement));

		// when
		associateService.update(communityId, associateId, "new-image", null, 11L, "New Intro");

		// then
		verify(associate).updateProfile("new-image", "OldName", newAchievement, "New Intro");
	}
}
