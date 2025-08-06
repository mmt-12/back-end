package com.memento.server.api.controller.memory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.annotation.CommunityId;
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

		return ResponseEntity.ok(
			memoryService.readAll(communityId, request.cursor(), request.size(), request.keyword(), request.startDate(),
				request.endDate())
		);
	}
}
