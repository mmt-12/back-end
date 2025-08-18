package com.memento.server.api.service.community;

import static com.memento.server.common.error.ErrorCodes.COMMUNITY_NOT_FOUND;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.community.dto.AssociateListResponse;
import com.memento.server.api.controller.member.dto.CommunityListResponse;
import com.memento.server.api.controller.community.dto.SearchAssociateResponse;
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

	private final AssociateRepository repository;
	private final CommunityRepository communityRepository;
	private final AchievementRepository achievementRepository;


	public AssociateListResponse searchAll(
		Long communityId,
		String keyword,
		Long cursor,
		Integer size
	) {
		Optional<Community> communityOptional = communityRepository.findById(communityId);
		if (communityOptional.isEmpty()) {
			throw new MementoException(COMMUNITY_NOT_FOUND);
		}

		List<Associate> associates = repository.findAllByCommunityIdAndKeywordWithCursor(
			communityId,
			keyword,
			cursor,
			PageRequest.of(0, size + 1)
		);

		return AssociateListResponse.from(associates, communityOptional.get(), size);
	}

	public CommunityListResponse searchAllMyAssociate(Long memberId) {
		List<Associate> associates = repository.findAllByMemberId(memberId);

		return CommunityListResponse.from(associates);
	}

	public Associate validAssociate(Long communityId, Long associateId){
		Associate associate = repository.findByIdAndDeletedAtNull(associateId)
			.orElseThrow(() -> new MementoException(ErrorCodes.ASSOCIATE_NOT_EXISTENCE));
		if(!communityId.equals(associate.getCommunity().getId())){
			throw new MementoException(ErrorCodes.ASSOCIATE_COMMUNITY_NOT_MATCH);
		}

		return associate;
	}

	public SearchAssociateResponse search(Long communityId, Long associateId) {
		Associate associate = validAssociate(communityId, associateId);

		Achievement achievement = associate.getAchievement();

		return SearchAssociateResponse.builder()
			.nickname(associate.getNickname())
			.achievement(achievement != null ?
				SearchAssociateResponse.Achievement.builder()
					.id(achievement.getId())
					.name(achievement.getName())
					.build()
				: null)
			.imageUrl(associate.getProfileImageUrl())
			.introduction(associate.getIntroduction())
			.birthday(associate.getMember().getBirthday())
			.build();
	}

	@Transactional
	public void update(Long communityId, Long associateId, String profileImageUrl, String nickname, Long achievementId, String introduction) {
		Associate associate = validAssociate(communityId, associateId);
		String newProfileImageUrl = associate.getProfileImageUrl();
		String newNickname = associate.getNickname();
		Achievement newAchievement = associate.getAchievement();
		String newIntroduction = associate.getIntroduction();

		if (profileImageUrl != null) {newProfileImageUrl = profileImageUrl;}
		if (nickname != null) {newNickname = nickname;}
		if (achievementId != null) {
			newAchievement = achievementRepository.findById(achievementId)
				.orElseThrow(() -> new MementoException(ErrorCodes.ACHIEVEMENT_NOT_EXISTENCE));}
		if (introduction != null) {newIntroduction = introduction;}

		associate.updateProfile(newProfileImageUrl, newNickname, newAchievement, newIntroduction);
	}
}
