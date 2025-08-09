package com.memento.server.api.service.achievement;

import java.util.List;

import org.springframework.stereotype.Service;

import com.memento.server.api.controller.achievement.dto.SearchAchievementResponse;
import com.memento.server.api.service.achievement.dto.SearchAchievementDto;
import com.memento.server.domain.achievement.AchievementRepository;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AchievementService {
	private final AssociateRepository associateRepository;
	private final AchievementRepository achievementRepository;

	public Associate validAssociate(Long communityId, Long associateId){
		Associate associate = associateRepository.findByIdAndDeletedAtNull(associateId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 참여자 입니다."));
		if(!communityId.equals(associate.getCommunity().getId())){
			throw new IllegalArgumentException("해당 커뮤니티의 참가자가 아닙니다.");
		}

		return associate;
	}

	public SearchAchievementResponse search(Long communityId, Long associateId) {
		Associate associate = validAssociate(communityId, associateId);

		List<SearchAchievementDto> achievements = achievementRepository.findAllWithObtainedRecord(associate.getId());

		List<SearchAchievementResponse.Achievement> achievementList = achievements.stream()
			.map(dto -> SearchAchievementResponse.Achievement.builder()
					.id(dto.id())
					.name(dto.name())
					.criteria(dto.criteria())
					.isObtained(dto.isObtained())
					.type(dto.type())
					.build()
			)
			.toList();

		return SearchAchievementResponse.builder()
			.achievements(achievementList)
			.build();
	}
}
