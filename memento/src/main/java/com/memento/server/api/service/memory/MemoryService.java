package com.memento.server.api.service.memory;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_EXISTENCE;
import static com.memento.server.common.error.ErrorCodes.COMMUNITY_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.MEMORY_NOT_AUTHOR;
import static com.memento.server.common.error.ErrorCodes.MEMORY_NOT_FOUND;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.memory.dto.CreateUpdateMemoryRequest;
import com.memento.server.api.controller.memory.dto.CreateUpdateMemoryResponse;
import com.memento.server.api.controller.memory.dto.DownloadImagesResponse;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryRequest;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.event.Event;
import com.memento.server.domain.event.EventRepository;
import com.memento.server.domain.event.Location;
import com.memento.server.domain.event.Period;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.MemoryAssociate;
import com.memento.server.domain.memory.MemoryAssociateRepository;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.memory.dto.MemoryAssociateCount;
import com.memento.server.domain.post.PostImage;
import com.memento.server.domain.post.PostImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoryService {

	private final MemoryRepository memoryRepository;
	private final MemoryAssociateRepository memoryAssociateRepository;
	private final PostImageRepository postImageRepository;
	private final CommunityRepository communityRepository;
	private final EventRepository eventRepository;
	private final AssociateRepository associateRepository;

	public ReadAllMemoryResponse readAll(Long communityId, ReadAllMemoryRequest request) {
		Long cursor = request.cursor();
		Integer size = request.size();
		String keyword = request.keyword();
		LocalDate startDate = request.startTime();
		LocalDate endDate = request.endTime();

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

	@Transactional
	public CreateUpdateMemoryResponse create(Long communityId, Long associateId, CreateUpdateMemoryRequest request) {
		Associate associate = associateRepository.findByIdAndDeletedAtIsNull(associateId)
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_EXISTENCE));
		Community community = communityRepository.findByIdAndDeletedAtIsNull(communityId)
			.orElseThrow(() -> new MementoException(COMMUNITY_NOT_FOUND));

		Event event = eventRepository.save(Event.builder()
			.title(request.title())
			.description(request.description())
			.location(Location.builder()
				.latitude(BigDecimal.valueOf(request.location().latitude()))
				.longitude(BigDecimal.valueOf(request.location().longitude()))
				.code(request.location().code())
				.name(request.location().name())
				.address(request.location().address())
				.build())
			.period(Period.builder()
				.startTime(request.period().startTime())
				.endTime(request.period().endTime())
				.build())
			.community(community)
			.associate(associate)
			.build());

		Memory memory = memoryRepository.save(Memory.builder()
			.event(event)
			.build());

		List<Associate> associates = associateRepository.findAllByIdInAndDeletedAtIsNull(request.associates());
		List<MemoryAssociate> memoryAssociates = new ArrayList<>();
		for (Associate other : associates) {
			memoryAssociates.add(MemoryAssociate.builder()
				.memory(memory)
				.associate(other)
				.build());
		}
		memoryAssociateRepository.saveAll(memoryAssociates);

		return CreateUpdateMemoryResponse.from(memory);
	}

	@Transactional
	public CreateUpdateMemoryResponse update(
		CreateUpdateMemoryRequest request,
		Long currentAssociateId,
		Long memoryId
	) {
		Memory memory = memoryRepository.findByIdAndDeletedAtIsNull(memoryId).orElseThrow(() -> new MementoException(MEMORY_NOT_FOUND));
		Event event = memory.getEvent();
		if (!event.getAssociate().getId().equals(currentAssociateId)) {
			throw new MementoException(MEMORY_NOT_AUTHOR);
		}

		event.update(request);

		List<MemoryAssociate> associates = memoryAssociateRepository.findAllByMemoryAndDeletedAtIsNull(memory);
		List<Long> checked = new ArrayList<>();

		List<MemoryAssociate> deleteList = new ArrayList<>();
		for (MemoryAssociate associate : associates) {
			Long associateId = associate.getId();
			checked.add(associateId);
			if (request.associates().contains(associateId))
				continue;
			deleteList.add(associate);
		}
		memoryAssociateRepository.deleteAll(deleteList);

		List<Long> newList = new ArrayList<>();
		for (Long associateId : request.associates()) {
			if (checked.contains(associateId))
				continue;
			newList.add(associateId);
		}
		List<Associate> newAssociates = associateRepository.findAllByIdInAndDeletedAtIsNull(newList);

		List<MemoryAssociate> newMemoryAssociates = new ArrayList<>();
		for (Associate associate : newAssociates) {
			newMemoryAssociates.add(MemoryAssociate.builder()
				.memory(memory)
				.associate(associate)
				.build());
		}
		memoryAssociateRepository.saveAll(newMemoryAssociates);

		return CreateUpdateMemoryResponse.from(memory);
	}

	@Transactional
	public void delete(Long memoryId, Long currentAssociateId) {
		Memory memory = memoryRepository.findByIdAndDeletedAtIsNull(memoryId).orElseThrow(() -> new MementoException(MEMORY_NOT_FOUND));
		Event event = memory.getEvent();
		if (!event.getAssociate().getId().equals(currentAssociateId)) {
			throw new MementoException(MEMORY_NOT_AUTHOR);
		}

		memoryRepository.delete(memory);
	}

	public DownloadImagesResponse downloadImages(Long memoryId) {
		Memory memory = memoryRepository.findByIdAndDeletedAtIsNull(memoryId).orElseThrow(() -> new MementoException(MEMORY_NOT_FOUND));
		List<PostImage> postImages = postImageRepository.findAllByMemory(memory);

		return DownloadImagesResponse.from(postImages);
	}
}
