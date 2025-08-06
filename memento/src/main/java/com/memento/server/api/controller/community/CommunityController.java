package com.memento.server.api.controller.community;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.annotation.MemberId;
import com.memento.server.api.controller.community.dto.CommunityListResponse;
import com.memento.server.api.service.community.CommunityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/communities")
public class CommunityController {

	private final CommunityService communityService;

	@GetMapping
	public ResponseEntity<CommunityListResponse> searchAll(@MemberId Long memberId) {
		return ResponseEntity.ok(CommunityListResponse.builder()
			.communities(communityService.searchAll(memberId))
			.build()
		);
	}
}
