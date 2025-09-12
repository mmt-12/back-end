package com.memento.server.api.service.achievement;

import java.util.List;

import org.springframework.stereotype.Service;

import com.memento.server.api.controller.achievement.dto.SearchAchievementResponse;
import com.memento.server.api.service.achievement.dto.SearchAchievementDto;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.achievement.Achievement;
import com.memento.server.domain.achievement.AchievementAssociate;
import com.memento.server.domain.achievement.AchievementAssociateRepository;
import com.memento.server.domain.achievement.AchievementRepository;
import com.memento.server.domain.achievement.CommonAchievementEvent;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AchievementService {
	private final AssociateRepository associateRepository;
	private final AchievementRepository achievementRepository;
	private final AchievementEventPublisher achievementEventPublisher;

	public Associate validAssociate(Long communityId, Long associateId){
		Associate associate = associateRepository.findByIdAndDeletedAtNull(associateId)
			.orElseThrow(() -> new MementoException(ErrorCodes.ASSOCIATE_NOT_EXISTENCE));
		if(!communityId.equals(associate.getCommunity().getId())){
			throw new MementoException(ErrorCodes.ASSOCIATE_COMMUNITY_NOT_MATCH);
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

	public void create(Long associateId, Long achievementId) {
		achievementEventPublisher.publishCommonAchievement(CommonAchievementEvent.from(associateId, achievementId));
	}
}
