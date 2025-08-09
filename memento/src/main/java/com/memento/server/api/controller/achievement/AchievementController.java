package com.memento.server.api.controller.achievement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.annotation.AssociateId;
import com.memento.server.annotation.CommunityId;
import com.memento.server.api.controller.achievement.dto.SearchAchievementResponse;
import com.memento.server.api.service.achievement.AchievementService;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/communities/{communityId}/associates/{associateId}/achievements")
@RequiredArgsConstructor
public class AchievementController {

	private final AchievementService achievementService;

	@GetMapping()
	public ResponseEntity<SearchAchievementResponse> read(
		@CommunityId Long currentCommunityId,
		@PathVariable Long communityId,
		@PathVariable Long associateId
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.ASSOCIATE_COMMUNITY_NOT_MATCH);
		}
		return ResponseEntity.ok(achievementService.search(currentCommunityId, associateId));
	}
}
