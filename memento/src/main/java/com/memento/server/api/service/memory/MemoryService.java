package com.memento.server.api.service.memory;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.memento.server.api.controller.memory.dto.CreateMemoryRequest;
import com.memento.server.api.controller.memory.dto.CreateMemoryResponse;
import com.memento.server.api.controller.memory.dto.DownloadImagesResponse;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse;
import com.memento.server.domain.memory.MemoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemoryService {

	private final MemoryRepository memoryRepository;

	public ReadAllMemoryResponse readAll(Long communityId, Long cursor, Long size, String keyword, LocalDate date,
		LocalDate date1) {
		return null;
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
