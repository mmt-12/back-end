package com.memento.server.api.controller.community;

import static com.memento.server.common.error.ErrorCodes.COMMUNITY_NOT_CURRENT;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.annotation.CommunityId;
import com.memento.server.api.controller.community.dto.AssociateListResponse;
import com.memento.server.api.controller.community.dto.ReadAssociateResponse;
import com.memento.server.api.controller.community.dto.UpdateAssociateRequest;
import com.memento.server.api.service.community.AssociateService;
import com.memento.server.common.exception.MementoException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/communities/{communityId}/associates")
public class AssociateController {

	private final AssociateService associateService;

	@GetMapping("/{associateId}")
	public ResponseEntity<ReadAssociateResponse> read(
		@PathVariable Long associateId
	) {
		return ResponseEntity.ok(ReadAssociateResponse.from());
	}

	@GetMapping
	public ResponseEntity<AssociateListResponse> searchAll(
		@CommunityId Long currentCommunityId,
		@PathVariable Long communityId,
		@RequestParam(defaultValue = "") String keyword,
		@RequestParam Long cursor,
		@RequestParam(defaultValue = "10") Integer size
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(COMMUNITY_NOT_CURRENT);
		}

		return ResponseEntity.ok(associateService.searchAll(communityId, keyword, cursor, size));
	}

	@PutMapping()
	public ResponseEntity<Void> update(
		UpdateAssociateRequest request
	) {
		return ResponseEntity.ok().build();
	}
}
