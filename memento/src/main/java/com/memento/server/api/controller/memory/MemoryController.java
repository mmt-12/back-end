package com.memento.server.api.controller.memory;

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
import com.memento.server.api.controller.memory.dto.CreateMemoryRequest;
import com.memento.server.api.controller.memory.dto.CreateMemoryResponse;
import com.memento.server.api.controller.memory.dto.DownloadImagesResponse;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryRequest;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse;
import com.memento.server.api.service.memory.MemoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/communities/{communityId}/memories")
public class MemoryController {

	private final MemoryService memoryService;

	@GetMapping
	public ResponseEntity<ReadAllMemoryResponse> read(@CommunityId Long currentCommunityId,
		@PathVariable Long communityId, ReadAllMemoryRequest request) {

		if (!currentCommunityId.equals(communityId)) {
			throw new IllegalArgumentException("다른 그룹의 요청입니다.");
		}

		return ResponseEntity.ok(memoryService.readAll(communityId, request));
	}

	@PostMapping
	public ResponseEntity<CreateMemoryResponse> create(
		@CommunityId Long currentCommunityId,
		@AssociateId Long associateId,
		@PathVariable Long communityId,
		@RequestBody CreateMemoryRequest request) {

		if (!currentCommunityId.equals(communityId)) {
			throw new IllegalArgumentException("다른 그룹의 요청입니다.");
		}

		return ResponseEntity.ok(memoryService.create(communityId, associateId, request));
	}

	@PutMapping("/{memoryId}")
	public ResponseEntity<CreateMemoryResponse> update(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long memoryId,
		CreateMemoryRequest request) {

		if (!currentCommunityId.equals(communityId)) {
			throw new IllegalArgumentException("다른 그룹의 요청입니다.");
		}

		return ResponseEntity.ok(memoryService.update(communityId, request, currentAssociateId, memoryId));
	}

	@DeleteMapping("/{memoryId}")
	public ResponseEntity<Void> delete(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long memoryId
	) {

		if (!currentCommunityId.equals(communityId)) {
			throw new IllegalArgumentException("다른 그룹의 요청입니다.");
		}
		memoryService.delete(communityId, memoryId, currentAssociateId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{memoryId}")
	public ResponseEntity<DownloadImagesResponse> downloadImages(
		@CommunityId Long currentCommunityId,
		@PathVariable Long communityId,
		@PathVariable Long memoryId
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new IllegalArgumentException("다른 그룹의 요청입니다.");
		}

		return ResponseEntity.ok(memoryService.downloadImages(communityId, memoryId));
	}
}
