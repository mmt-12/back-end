package com.memento.server.api.controller.mbti;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.annotation.AssociateId;
import com.memento.server.annotation.CommunityId;
import com.memento.server.api.controller.mbti.dto.CreateMbtiRequest;
import com.memento.server.api.controller.mbti.dto.SearchMbtiResponse;
import com.memento.server.api.service.mbti.MbtiService;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/communities/{communityId}/associates/{associateId}/mbti-tests")
@RequiredArgsConstructor
public class MbtiController {

	private final MbtiService mbtiService;

	@PostMapping()
	public ResponseEntity<Void> create(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long associateId,
		@RequestBody @Valid CreateMbtiRequest request
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}
		if(currentAssociateId.equals(associateId)) {
			throw new MementoException(ErrorCodes.ASSOCIATE_NOT_AUTHORITY);
		}

		mbtiService.create(communityId, currentAssociateId, associateId, request.mbti());
		return ResponseEntity.ok().build();
	}

	@GetMapping()
	public ResponseEntity<SearchMbtiResponse> search(
		@CommunityId Long currentCommunityId,
		@PathVariable Long communityId,
		@PathVariable Long associateId
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}
		return ResponseEntity.ok(mbtiService.search(communityId, associateId));
	}
}
