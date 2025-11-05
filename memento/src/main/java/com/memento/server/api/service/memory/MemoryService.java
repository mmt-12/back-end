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
import com.memento.server.api.controller.memory.dto.ReadMemoryListRequest;
import com.memento.server.api.controller.memory.dto.ReadMemoryListResponse;
import com.memento.server.api.controller.memory.dto.ReadMemoryResponse;
import com.memento.server.api.service.achievement.AchievementEventPublisher;
import com.memento.server.api.service.fcm.FCMEventPublisher;
import com.memento.server.api.service.fcm.dto.event.MemoryFCM;
import com.memento.server.api.service.memory.dto.Author;
import com.memento.server.api.service.memory.dto.MemoryItem;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.achievement.Achievement;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.event.Event;
import com.memento.server.domain.event.EventRepository;
import com.memento.server.domain.event.Location;
import com.memento.server.domain.event.Period;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.MemoryAchievementEvent;
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
	private final AchievementEventPublisher achievementEventPublisher;
	private final FCMEventPublisher fcmEventPublisher;

	public ReadMemoryResponse read(Long memoryId) {
		Memory memory = memoryRepository.findById(memoryId).orElseThrow(() -> new MementoException(MEMORY_NOT_FOUND));

		List<PostImage> images = postImageRepository.findAllByMemoryId(memoryId);
		Long associateCount = memoryAssociateRepository.countAssociatesByMemoryId(memoryId);

		Associate associate = memory.getEvent().getAssociate();
		Achievement achievement = associate.getAchievement();
		Author author = Author.of(associate, achievement);
		List<MemoryAssociate> memoryAssociates = memoryAssociateRepository.findAllByMemoryAndDeletedAtIsNull(memory);
		List<Associate> associates = new ArrayList<>();
		for (MemoryAssociate memoryAssociate : memoryAssociates) {
			associates.add(memoryAssociate.getAssociate());
		}

		return ReadMemoryResponse.of(memory, images, associateCount, author, associates);
	}

	public ReadMemoryListResponse readAll(Long communityId, ReadMemoryListRequest request) {
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

		List<MemoryItem> memories = memoryRepository.findAllByConditions(
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
		Long nextCursor = memories.isEmpty() ? null : memories.getLast().id();

		List<Long> memoryIds = memories.stream().map(MemoryItem::id).toList();
		List<PostImage> images = postImageRepository.findAllByMemoryIds(memoryIds);
		List<MemoryAssociateCount> associateCounts = memoryAssociateRepository.countAssociatesByMemoryIds(memoryIds);

		return ReadMemoryListResponse.of(memories, images, associateCounts, hasNext, nextCursor);
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

		achievementEventPublisher.publishMemoryAchievement(MemoryAchievementEvent.from(List.of(associate.getId()), MemoryAchievementEvent.Type.CREATE));
		achievementEventPublisher.publishMemoryAchievement(MemoryAchievementEvent.from(associates.stream().map(Associate::getId).toList(), MemoryAchievementEvent.Type.JOINED));
		fcmEventPublisher.publishNotification(MemoryFCM.from(memory.getId(), associateId));

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

		List<MemoryAssociate> memoryAssociates = memoryAssociateRepository.findAllByMemoryAndDeletedAtIsNull(memory);
		List<Long> checked = new ArrayList<>();

		List<MemoryAssociate> deleteList = new ArrayList<>();
		for (MemoryAssociate memoryAssociate : memoryAssociates) {
			Long associateId = memoryAssociate.getAssociate().getId();
			checked.add(associateId);
			if (request.associates().contains(associateId))
				continue;
			deleteList.add(memoryAssociate);
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
		achievementEventPublisher.publishMemoryAchievement(MemoryAchievementEvent.from(newList, MemoryAchievementEvent.Type.JOINED));

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
