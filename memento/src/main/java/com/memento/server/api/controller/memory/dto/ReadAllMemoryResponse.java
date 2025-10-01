package com.memento.server.api.controller.memory.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse.MemoryResponse.AuthorResponse;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse.MemoryResponse.LocationResponse;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse.MemoryResponse.PeriodResponse;
import com.memento.server.api.service.memory.dto.Author;
import com.memento.server.api.service.memory.dto.Author.Achievement;
import com.memento.server.domain.event.Event;
import com.memento.server.domain.event.Location;
import com.memento.server.domain.event.Period;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.dto.MemoryAssociateCount;
import com.memento.server.domain.post.PostImage;

import lombok.Builder;

@Builder
public record ReadAllMemoryResponse(
	Long nextCursor,
	Boolean hasNext,
	List<MemoryResponse> memories
) {
	@Builder
	public record MemoryResponse(
		Long id,
		String title,
		String description,
		PeriodResponse period,
		LocationResponse location,
		Integer memberAmount,
		Integer pictureAmount,
		List<String> pictures,
		AuthorResponse author
	) {

		@Builder
		public record AuthorResponse(
			Long id,
			String imageUrl,
			String nickname,
			AchievementResponse achievement
		) {
			@Builder
			public record AchievementResponse(
				Long id,
				String name
			) {
				public static AchievementResponse from(Achievement achievement) {
					return AchievementResponse.builder()
						.id(achievement.id())
						.name(achievement.name())
						.build();
				}
			}

			public static AuthorResponse from(Author author) {
				return AuthorResponse.builder()
					.id(author.id())
					.imageUrl(author.imageUrl())
					.nickname(author.nickname())
					.achievement(author.achievement() == null ? null : AchievementResponse.from(author.achievement()))
					.build();
			}
		}

		@Builder
		public record PeriodResponse(
			LocalDateTime startTime,
			LocalDateTime endTime
		) {
			public static PeriodResponse from(Period period) {
				return PeriodResponse.builder()
					.startTime(period.getStartTime())
					.endTime(period.getEndTime())
					.build();
			}
		}

		@Builder
		public record LocationResponse(
			String address,
			String name,
			Float latitude,
			Float longitude,
			Integer code
		) {
			public static LocationResponse from(Location location) {
				return LocationResponse.builder()
					.address(location.getAddress())
					.name(location.getName())
					.latitude(location.getLatitude().floatValue())
					.longitude(location.getLongitude().floatValue())
					.code(location.getCode())
					.build();
			}
		}
	}

	public static ReadAllMemoryResponse from(
		List<Memory> memories,
		List<PostImage> images,
		List<MemoryAssociateCount> associateCounts,
		boolean hasNext,
		Long nextCursor,
		Map<Long, Author> authorMap
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

		List<MemoryResponse> memoryResponses = new ArrayList<>();
		for (Memory memory : memories) {
			Event event = memory.getEvent();
			List<String> pictures = pictureMap.getOrDefault(memory.getId(), new ArrayList<>());

			memoryResponses.add(
				MemoryResponse.builder()
					.id(memory.getId())
					.title(event.getTitle())
					.description(event.getDescription())
					.period(PeriodResponse.from(event.getPeriod()))
					.location(LocationResponse.from(event.getLocation()))
					.memberAmount(associatesCountMap.getOrDefault(memory.getId(), 0))
					.pictureAmount(pictures.size())
					.pictures(pictures.subList(0, Math.min(pictures.size(), 9)))
					.author(AuthorResponse.from(authorMap.get(memory.getId())))
					.build()
			);
		}

		return ReadAllMemoryResponse.builder()
			.hasNext(hasNext)
			.nextCursor(nextCursor)
			.memories(memoryResponses)
			.build();
	}
}
