package com.memento.server.api.service.community;

import static com.memento.server.common.error.ErrorCodes.COMMUNITY_NOT_FOUND;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.community.dto.response.CommunityAssociateListResponse;
import com.memento.server.api.controller.member.dto.CommunityListResponse;
import com.memento.server.api.service.community.dto.response.SearchAssociateResponse;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.achievement.Achievement;
import com.memento.server.domain.achievement.AchievementRepository;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssociateService {

	private final AssociateRepository associateRepository;
	private final CommunityRepository communityRepository;
	private final AchievementRepository achievementRepository;

	public CommunityAssociateListResponse searchAll(
		Long communityId,
		String keyword
	) {
		Community community = communityRepository.findByIdAndDeletedAtIsNull(communityId)
			.orElseThrow(() -> new MementoException(COMMUNITY_NOT_FOUND));
		List<Associate> associates = associateRepository.findAllByCommunityIdAndKeyword(communityId, keyword);

		return CommunityAssociateListResponse.from(associates, community);
	}

	public CommunityListResponse searchAllMyAssociate(Long memberId) {
		List<Associate> associates = associateRepository.findAllByMemberIdAndDeletedAtIsNull(memberId);

		return CommunityListResponse.from(associates);
	}

	public Associate validAssociate(Long communityId, Long associateId) {
		Associate associate = associateRepository.findByIdAndDeletedAtNull(associateId)
			.orElseThrow(() -> new MementoException(ErrorCodes.ASSOCIATE_NOT_EXISTENCE));
		if (!communityId.equals(associate.getCommunity().getId())) {
			throw new MementoException(ErrorCodes.ASSOCIATE_COMMUNITY_NOT_MATCH);
		}

		return associate;
	}

	public SearchAssociateResponse search(Long communityId, Long associateId) {
		Associate associate = validAssociate(communityId, associateId);

		Achievement achievement = associate.getAchievement();

		return SearchAssociateResponse.of(associate, achievement);
	}

	@Transactional
	public void update(Long communityId, Long associateId, String profileImageUrl, String nickname, Long achievementId,
		String introduction) {
		Associate associate = validAssociate(communityId, associateId);
		String newProfileImageUrl = associate.getProfileImageUrl();
		String newNickname = associate.getNickname();
		Achievement newAchievement = associate.getAchievement();
		String newIntroduction = associate.getIntroduction();

		if (profileImageUrl != null) {
			newProfileImageUrl = profileImageUrl;
		}
		if (nickname != null) {
			newNickname = nickname;
		}
		if (achievementId != null) {
			newAchievement = achievementRepository.findById(achievementId)
				.orElseThrow(() -> new MementoException(ErrorCodes.ACHIEVEMENT_NOT_EXISTENCE));
		}
		if (introduction != null) {
			newIntroduction = introduction;
		}

		associate.updateProfile(newProfileImageUrl, newNickname, newAchievement, newIntroduction);
	}
}
