package com.memento.server.api.service.community;

import org.springframework.stereotype.Service;

import com.memento.server.api.controller.community.dto.AssociateListResponse;
import com.memento.server.api.controller.community.dto.SearchAssociateResponse;
import com.memento.server.domain.achievement.Achievement;
import com.memento.server.domain.achievement.AchievementRepository;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssociateService {

	private final AssociateRepository repository;
	private final AchievementRepository achievementRepository;

	public Associate validAssociate(Long communityId, Long associateId){
		Associate associate = repository.findByIdAndDeletedAtNull(associateId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 참여자 입니다."));
		if(!communityId.equals(associate.getCommunity().getId())){
			throw new IllegalArgumentException("해당 커뮤니티의 참가자가 아닙니다.");
		}

		return associate;
	}

	public AssociateListResponse searchAll(Long communityId, String keyword, Long cursor, Integer size) {
		return null;
	}

	public SearchAssociateResponse search(Long communityId, Long associateId) {
		Associate associate = validAssociate(communityId, associateId);

		return SearchAssociateResponse.builder()
			.nickname(associate.getNickname())
			.achievement(SearchAssociateResponse.Achievement.builder()
				.id(associate.getAchievement().getId())
				.name(associate.getAchievement().getName())
				.build())
			.imageUrl(associate.getProfileImageUrl())
			.introduction(associate.getIntroduction())
			.birthday(associate.getMember().getBirthday())
			.build();
	}

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
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 업적입니다."));}
		if (introduction != null) {newIntroduction = introduction;}

		associate.updateProfile(newProfileImageUrl, newNickname, newAchievement, newIntroduction);
	}
}
