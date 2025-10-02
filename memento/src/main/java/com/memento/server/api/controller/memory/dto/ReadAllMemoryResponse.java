package com.memento.server.api.controller.memory.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse.MemoryResponse.AuthorResponse;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse.MemoryResponse.LocationResponse;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse.MemoryResponse.PeriodResponse;
import com.memento.server.api.service.memory.dto.MemoryItem;
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
				public static AchievementResponse from(MemoryItem.AchievementDto achievement) {
					return achievement == null ? null : AchievementResponse.builder()
						.id(achievement.id())
						.name(achievement.name())
						.build();
				}
			}

			public static AuthorResponse from(
				MemoryItem.AssociateDto author,
				MemoryItem.AchievementDto achievement
			) {
				return AuthorResponse.builder()
					.id(author.id())
					.imageUrl(author.profileImageUrl())
					.nickname(author.nickname())
					.achievement(AchievementResponse.from(achievement))
					.build();
			}
		}

		@Builder
		public record PeriodResponse(
			LocalDateTime startTime,
			LocalDateTime endTime
		) {
			public static PeriodResponse from(MemoryItem.PeriodDto period) {
				return PeriodResponse.builder()
					.startTime(period.startTime())
					.endTime(period.endTime())
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
			public static LocationResponse from(MemoryItem.LocationDto location) {
				return LocationResponse.builder()
					.address(location.address())
					.name(location.name())
					.latitude(location.latitude().floatValue())
					.longitude(location.longitude().floatValue())
					.code(location.code())
					.build();
			}
		}
	}

	public static ReadAllMemoryResponse from(
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

		List<MemoryResponse> memoryResponses = new ArrayList<>();
		for (MemoryItem memory : memories) {
			List<String> pictures = pictureMap.getOrDefault(memory.id(), new ArrayList<>());

			memoryResponses.add(
				MemoryResponse.builder()
					.id(memory.id())
					.title(memory.title())
					.description(memory.description())
					.period(PeriodResponse.from(memory.period()))
					.location(LocationResponse.from(memory.location()))
					.memberAmount(associatesCountMap.getOrDefault(memory.id(), 0))
					.pictureAmount(pictures.size())
					.pictures(pictures.subList(0, Math.min(pictures.size(), 9)))
					.author(AuthorResponse.from(memory.associate(), memory.achievement()))
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
