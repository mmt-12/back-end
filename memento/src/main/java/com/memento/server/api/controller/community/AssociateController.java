package com.memento.server.api.controller.community;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.annotation.AssociateId;
import com.memento.server.annotation.CommunityId;
import com.memento.server.api.controller.community.dto.SearchAssociateResponse;
import com.memento.server.api.controller.community.dto.UpdateAssociateRequest;
import com.memento.server.api.controller.community.dto.AssociateListResponse;
import com.memento.server.api.service.community.AssociateService;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/communities/{communityId}/associates")
public class AssociateController {

	private final AssociateService associateService;

	@GetMapping("/{associateId}")
	public ResponseEntity<SearchAssociateResponse> search(
		@CommunityId Long currentCommunityId,
		@PathVariable Long communityId,
		@PathVariable Long associateId
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}

		return ResponseEntity.ok(associateService.search(communityId, associateId));
	}

	@GetMapping
	public ResponseEntity<AssociateListResponse> searchAll(
		@CommunityId Long currentCommunityId,
		@PathVariable Long communityId,
		@RequestParam(defaultValue = "") String keyword,
		@RequestParam(defaultValue = "0") Long cursor,
		@RequestParam(defaultValue = "10") Integer size) {

		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}

		return ResponseEntity.ok(associateService.searchAll(communityId, keyword, cursor, size));
	}

	@PutMapping()
	public ResponseEntity<Void> update(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@Valid @RequestBody UpdateAssociateRequest request
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}
		associateService.update(
			communityId,
			currentAssociateId,
			request.profileImageUrl(),
			request.nickname(),
			request.achievement(),
			request.introduction());
		return ResponseEntity.ok().build();
	}
}
