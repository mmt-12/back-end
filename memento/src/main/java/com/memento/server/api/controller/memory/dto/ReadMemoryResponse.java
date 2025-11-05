package com.memento.server.api.controller.memory.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.memento.server.api.controller.community.dto.response.AssociateResponse;
import com.memento.server.api.service.memory.dto.Author;
import com.memento.server.api.service.memory.dto.MemoryItem;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.event.Event;
import com.memento.server.domain.event.Location;
import com.memento.server.domain.event.Period;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.post.PostImage;

import lombok.Builder;

@Builder
public record ReadMemoryResponse(
	Long id,
	String title,
	String description,
	PeriodResponse period,
	LocationResponse location,
	Integer memberAmount,
	Integer pictureAmount,
	List<String> pictures,
	AuthorResponse author,
	List<AssociateResponse> associates
) {
	@Builder
	public record AuthorResponse(
		Long id,
		String imageUrl,
		String nickname,
		AchievementResponse achievement
	) {
		public static AuthorResponse of(MemoryItem.AssociateDto associate, MemoryItem.AchievementDto achievement) {
			return AuthorResponse.builder()
				.id(associate.id())
				.imageUrl(associate.profileImageUrl())
				.nickname(associate.nickname())
				.achievement(achievement == null ? null : AchievementResponse.from(achievement))
				.build();
		}

		@Builder
		public record AchievementResponse(
			Long id,
			String name
		) {
			public static AchievementResponse from(
				Author.Achievement achievement) {
				return AchievementResponse.builder()
					.id(achievement.id())
					.name(achievement.name())
					.build();
			}

			public static AchievementResponse from(MemoryItem.AchievementDto achievement) {
				return AchievementResponse.builder()
					.id(achievement.id())
					.name(achievement.name())
					.build();
			}
		}

		public static AuthorResponse of(Author author) {
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
		public static LocationResponse from(Location location) {
			return LocationResponse.builder()
				.address(location.getAddress())
				.name(location.getName())
				.latitude(location.getLatitude().floatValue())
				.longitude(location.getLongitude().floatValue())
				.code(location.getCode())
				.build();
		}

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

	public static ReadMemoryResponse of(
		Memory memory,
		List<PostImage> images,
		Long associateCount,
		Author author,
		List<Associate> associates
	) {
		Event event = memory.getEvent();
		List<String> pictures = new ArrayList<>();

		for (PostImage image : images) {
			pictures.add(image.getUrl());
			if (pictures.size() >= 9)
				break;
		}

		return ReadMemoryResponse.builder()
			.id(memory.getId())
			.title(event.getTitle())
			.description(event.getDescription())
			.period(PeriodResponse.from(event.getPeriod()))
			.location(LocationResponse.from(event.getLocation()))
			.memberAmount(Math.toIntExact(associateCount))
			.pictureAmount(images.size())
			.pictures(pictures)
			.author(AuthorResponse.of(author))
			.associates(AssociateResponse.from(associates))
			.build();
	}

	public static ReadMemoryResponse of(
		Memory memory,
		List<PostImage> images,
		Long associateCount,
		Author author
	) {
		Event event = memory.getEvent();
		List<String> pictures = new ArrayList<>();

		for (PostImage image : images) {
			pictures.add(image.getUrl());
			if (pictures.size() >= 9)
				break;
		}

		return ReadMemoryResponse.builder()
			.id(memory.getId())
			.title(event.getTitle())
			.description(event.getDescription())
			.period(PeriodResponse.from(event.getPeriod()))
			.location(LocationResponse.from(event.getLocation()))
			.memberAmount(Math.toIntExact(associateCount))
			.pictureAmount(images.size())
			.pictures(pictures)
			.author(AuthorResponse.of(author))
			.build();
	}

	public static ReadMemoryResponse of(
		MemoryItem memory,
		List<String> pictures,
		Integer associateCount
	) {
		return ReadMemoryResponse.builder()
			.id(memory.id())
			.title(memory.title())
			.description(memory.description())
			.period(PeriodResponse.from(memory.period()))
			.location(LocationResponse.from(memory.location()))
			.memberAmount(Math.toIntExact(associateCount))
			.pictureAmount(pictures.size())
			.pictures(pictures)
			.author(AuthorResponse.of(memory.associate(), memory.achievement()))
			.build();
	}
}
