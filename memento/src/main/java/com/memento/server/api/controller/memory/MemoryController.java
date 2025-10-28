package com.memento.server.api.controller.memory;

import static com.memento.server.common.error.ErrorCodes.COMMUNITY_NOT_MATCH;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.annotation.AssociateId;
import com.memento.server.annotation.CommunityId;
import com.memento.server.api.controller.memory.dto.CreateUpdateMemoryRequest;
import com.memento.server.api.controller.memory.dto.CreateUpdateMemoryResponse;
import com.memento.server.api.controller.memory.dto.DownloadImagesResponse;
import com.memento.server.api.controller.memory.dto.ReadMemoryListRequest;
import com.memento.server.api.controller.memory.dto.ReadMemoryListResponse;
import com.memento.server.api.controller.memory.dto.ReadMemoryResponse;
import com.memento.server.api.service.memory.MemoryService;
import com.memento.server.common.exception.MementoException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/communities/{communityId}/memories")
public class MemoryController {

	private final MemoryService memoryService;

	@GetMapping
	public ResponseEntity<ReadMemoryListResponse> read(
		@CommunityId Long currentCommunityId,
		@PathVariable Long communityId,
		ReadMemoryListRequest request
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(COMMUNITY_NOT_MATCH);
		}

		return ResponseEntity.ok(memoryService.readAll(communityId, request));
	}

	@GetMapping("/{memoryId}")
	public ResponseEntity<ReadMemoryResponse> read(
		@CommunityId Long currentCommunityId,
		@PathVariable Long communityId,
		@PathVariable Long memoryId
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(COMMUNITY_NOT_MATCH);
		}

		return ResponseEntity.ok(memoryService.read(memoryId));
	}

	@PostMapping
	public ResponseEntity<CreateUpdateMemoryResponse> create(
		@CommunityId Long currentCommunityId,
		@AssociateId Long associateId,
		@PathVariable Long communityId,
		@RequestBody CreateUpdateMemoryRequest request
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(COMMUNITY_NOT_MATCH);
		}

		return ResponseEntity.ok(memoryService.create(communityId, associateId, request));
	}

	@PutMapping("/{memoryId}")
	public ResponseEntity<CreateUpdateMemoryResponse> update(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long memoryId,
		CreateUpdateMemoryRequest request
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(COMMUNITY_NOT_MATCH);
		}

		return ResponseEntity.ok(memoryService.update(request, currentAssociateId, memoryId));
	}

	@DeleteMapping("/{memoryId}")
	public ResponseEntity<Void> delete(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long memoryId
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(COMMUNITY_NOT_MATCH);
		}
		memoryService.delete(memoryId, currentAssociateId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{memoryId}/images")
	public ResponseEntity<DownloadImagesResponse> downloadImages(
		@CommunityId Long currentCommunityId,
		@PathVariable Long communityId,
		@PathVariable Long memoryId
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(COMMUNITY_NOT_MATCH);
		}

		return ResponseEntity.ok(memoryService.downloadImages(memoryId));
	}
}
