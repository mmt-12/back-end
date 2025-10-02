package com.memento.server.api.service.memory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MemoryItem(
	Long id,

	String title,
	String description,

	PeriodDto period,
	LocationDto location,

	AssociateDto associate,
	AchievementDto achievement
) {
	public record PeriodDto(
		LocalDateTime startTime,
		LocalDateTime endTime
	) {}

	public record LocationDto(
		BigDecimal latitude,
		BigDecimal longitude,
		Integer code,
		String name,
		String address
	) {}

	public record AssociateDto(
		Long id,
		String nickname,
		String profileImageUrl
	) {}

	public record AchievementDto(
		Long id,
		String name
	) {}

	public MemoryItem(
		Long id,
		String title,
		String description,
		LocalDateTime startTime,
		LocalDateTime endTime,
		BigDecimal latitude,
		BigDecimal longitude,
		Integer code,
		String name,
		String address,
		Long associateId,
		String associateNickname,
		String associateProfileImageUrl,
		Long achievementId,
		String achievementName
	) {
		this(
			id,
			title,
			description,
			new PeriodDto(startTime, endTime),
			new LocationDto(latitude, longitude, code, name, address),
			new AssociateDto(associateId, associateNickname, associateProfileImageUrl),
			achievementId == null ? null : new AchievementDto(achievementId, achievementName)
		);
	}
}

