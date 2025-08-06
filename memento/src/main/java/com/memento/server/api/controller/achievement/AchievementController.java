package com.memento.server.api.controller.achievement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.api.controller.achievement.dto.ReadAchievementResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/communities/{communityId}/associates/{associateId}/achievements")
@RequiredArgsConstructor
public class AchievementController {

	@GetMapping()
	public ResponseEntity<ReadAchievementResponse> read(
		@PathVariable Long associateId
	) {
		return ResponseEntity.ok(ReadAchievementResponse.from());
	}
}
