package com.memento.server.spring.api.service.achievement;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.achievement.dto.SearchAchievementResponse;
import com.memento.server.api.service.achievement.dto.SearchAchievementDto;
import com.memento.server.domain.achievement.AchievementType;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.Community;
import com.memento.server.spring.api.service.ServiceTestSupport;

public class AchievementServiceTest extends ServiceTestSupport {

	@Test
	@DisplayName("업적 조회")
	void searchTest() {
		// given
		Long communityId = 1L;
		Long associateId = 2L;

		Associate associate = mock(Associate.class);
		Community community = mock(Community.class);

		when(associateRepository.findByIdAndDeletedAtNull(associateId))
			.thenReturn(Optional.of(associate));
		when(associate.getCommunity()).thenReturn(community);
		when(community.getId()).thenReturn(communityId);
		when(associate.getId()).thenReturn(associateId);

		List<SearchAchievementDto> dtoList = List.of(
			new SearchAchievementDto(1L, "시간빌게이츠", "연속 출석 수 15일 이상", AchievementType.OPEN, true),
			new SearchAchievementDto(2L, "관상가", "모든 그룹원의 MBTI 테스트 참여", AchievementType.OPEN, true)
		);

		when(achievementRepository.findAllWithObtainedRecord(associateId))
			.thenReturn(dtoList);

		// when
		SearchAchievementResponse response = achievementService.search(communityId, associateId);

		// then
		assertThat(response.achievements()).hasSize(2);
		assertThat(response.achievements().get(0).getName()).isEqualTo("시간빌게이츠");
		assertThat(response.achievements().get(1).isObtained()).isTrue();

		verify(associateRepository).findByIdAndDeletedAtNull(associateId);
		verify(achievementRepository).findAllWithObtainedRecord(associateId);
	}

}
