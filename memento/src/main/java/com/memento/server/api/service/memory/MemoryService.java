package com.memento.server.api.service.memory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.memento.server.api.controller.memory.dto.CreateMemoryRequest;
import com.memento.server.api.controller.memory.dto.CreateMemoryResponse;
import com.memento.server.api.controller.memory.dto.DownloadImagesResponse;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryRequest;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.MemoryAssociateRepository;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.memory.dto.MemoryAssociateCount;
import com.memento.server.domain.post.PostImage;
import com.memento.server.domain.post.PostImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemoryService {

	private final MemoryRepository memoryRepository;
	private final MemoryAssociateRepository memoryAssociateRepository;
	private final PostImageRepository postImageRepository;

	public ReadAllMemoryResponse readAll(Long communityId, ReadAllMemoryRequest request) {
		Long cursor = request.cursor();
		Integer size = request.size();
		String keyword = request.keyword();
		LocalDate startDate = request.startDate();
		LocalDate endDate = request.endDate();

		LocalDateTime startDateTime = null;
		LocalDateTime endDateTime = null;

		if (startDate != null) {
			startDateTime = startDate.atStartOfDay();
		}
		if (endDate != null) {
			endDateTime = endDate.atTime(LocalTime.MAX);
		}

		List<Memory> memories = memoryRepository.findAllByConditions(
			communityId,
			keyword,
			startDateTime,
			endDateTime,
			cursor,
			PageRequest.of(0, size + 1)
		);

		boolean hasNext = false;
		if (memories.size() > size) {
			hasNext = true;
			memories = memories.subList(0, size);
		}
		Long nextCursor = memories.isEmpty() ? null : memories.getLast().getId();

		List<Long> memoryIds = memories.stream().map(Memory::getId).toList();
		List<PostImage> images = postImageRepository.findAllByMemoryIds(memoryIds);
		List<MemoryAssociateCount> associateCounts = memoryAssociateRepository.countAssociatesByMemoryIds(memoryIds);

		return ReadAllMemoryResponse.from(memories, images, associateCounts, hasNext, nextCursor);
	}

	public CreateMemoryResponse create(Long communityId, CreateMemoryRequest request) {
		return null;
	}

	public CreateMemoryResponse update(Long communityId, CreateMemoryRequest request, Long currentAssociateId,
		Long memoryId) {
		return null;
	}

	public void delete(Long communityId, Long memoryId, Long currentAssociateId) {
	}

	public DownloadImagesResponse downloadImages(Long communityId, Long memoryId) {
		return null;
	}
}
