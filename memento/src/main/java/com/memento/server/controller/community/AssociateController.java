package com.memento.server.controller.community;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.annotation.CommunityId;
import com.memento.server.service.community.AssociateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/communities/{communityId}/associates")
public class AssociateController {

	private final AssociateService associateService;

	@GetMapping
	public ResponseEntity<AssociateListResponse> searchAll(
		@CommunityId Long currentCommunityId,
		@PathVariable Long communityId,
		@RequestParam(defaultValue = "") String keyword,
		@RequestParam(defaultValue = "0") Long cursor,
		@RequestParam(defaultValue = "10") Integer size) {

		if (!currentCommunityId.equals(communityId)) {
			throw new IllegalArgumentException("다른 그룹의 요청입니다.");
		}

		return ResponseEntity.ok(associateService.searchAll(communityId, keyword, cursor, size));
	}
}
