package com.memento.server.api.controller.memory.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.memento.server.api.service.memory.dto.MemoryItem;
import com.memento.server.domain.memory.dto.MemoryAssociateCount;
import com.memento.server.domain.post.PostImage;

import lombok.Builder;

@Builder
public record ReadMemoryListResponse(
	Long nextCursor,
	Boolean hasNext,
	List<ReadMemoryResponse> memories
) {
	public static ReadMemoryListResponse of(
		List<MemoryItem> memories,
		List<PostImage> images,
		List<MemoryAssociateCount> associateCounts,
		boolean hasNext,
		Long nextCursor
	) {
		Map<Long, List<String>> pictureMap = new HashMap<>();
		for (PostImage postImage : images) {
			Long memoryId = postImage.getPost().getMemory().getId();
			List<String> pictureList = pictureMap.getOrDefault(memoryId, new ArrayList<>());
			pictureList.add(postImage.getUrl());
			pictureMap.put(memoryId, pictureList);
		}

		Map<Long, Integer> associatesCountMap = new HashMap<>();
		for (MemoryAssociateCount associateCount : associateCounts) {
			associatesCountMap.put(associateCount.memoryId(), associateCount.associateCount().intValue());
		}

		List<ReadMemoryResponse> memoryResponses = new ArrayList<>();
		for (MemoryItem memory : memories) {
			memoryResponses.add(ReadMemoryResponse.of(memory, pictureMap.getOrDefault(memory.id(), new ArrayList<>()),
				associatesCountMap.getOrDefault(memory.id(), 0)));
		}

		return ReadMemoryListResponse.builder()
			.hasNext(hasNext)
			.nextCursor(nextCursor)
			.memories(memoryResponses)
			.build();
	}
}
