package com.memento.server.api.service.mbti;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.mbti.dto.SearchMbtiResponse;
import com.memento.server.api.service.achievement.AchievementEventPublisher;
import com.memento.server.api.service.eventMessage.EventMessagePublisher;
import com.memento.server.api.service.eventMessage.dto.MbtiNotification;
import com.memento.server.api.service.mbti.dto.MbtiSearchDto;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.mbti.Mbti;
import com.memento.server.domain.mbti.MbtiAchievementEvent;
import com.memento.server.domain.mbti.MbtiTest;
import com.memento.server.domain.mbti.MbtiTestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MbtiService {

	private final AssociateRepository associateRepository;
	private final MbtiTestRepository mbtiTestRepository;
	private final EventMessagePublisher eventMessagePublisher;
	private final AchievementEventPublisher achievementEventPublisher;

	public Associate validAssociate(Long communityId, Long associateId){
		Associate associate = associateRepository.findByIdAndDeletedAtNull(associateId)
			.orElseThrow(() -> new MementoException(ErrorCodes.ASSOCIATE_NOT_EXISTENCE));
		if(!communityId.equals(associate.getCommunity().getId())){
			throw new MementoException(ErrorCodes.ASSOCIATE_COMMUNITY_NOT_MATCH);
		}

		return associate;
	}

	@Transactional
	public void create(Long communityId, Long from, Long to, Mbti mbti) {
		Associate fromAssociate = validAssociate(communityId, from);
		Associate toAssociate = validAssociate(communityId, to);

		MbtiTest existing = mbtiTestRepository.findByFromAssociateIdAndToAssociateId(fromAssociate.getId(), toAssociate.getId());

		if (existing != null) {
			existing.updateMbti(mbti);
			return;
		}

		mbtiTestRepository.save(MbtiTest.builder()
			.fromAssociate(fromAssociate)
			.toAssociate(toAssociate)
			.mbti(mbti)
			.build());
		eventMessagePublisher.publishNotification(MbtiNotification.from(toAssociate.getId()));
		achievementEventPublisher.publishMbtiAchievement(MbtiAchievementEvent.from(fromAssociate.getId(), toAssociate.getId()));
	}

	public SearchMbtiResponse search(Long communityId, Long associateId) {
		Associate associate = validAssociate(communityId, associateId);

		MbtiSearchDto counts = mbtiTestRepository.countMbtiByToAssociate(associate.getId());

		return SearchMbtiResponse.from(counts);
	}
}
