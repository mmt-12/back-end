package com.memento.server.api.service.fcm.dto;

import static com.memento.server.domain.notification.NotificationType.MEMORY;

import java.util.HashMap;
import java.util.Map;

import com.memento.server.domain.notification.NotificationType;

import lombok.Builder;

@Builder
public record MemoryData(
	NotificationType type,
	Long memoryId
) implements FCMData {

	@Override
	public Map<String, String> toDataMap() {
		Map<String, String> data = new HashMap<>();
		data.put("type", type.name());
		data.put("memoryId", String.valueOf(memoryId));
		return data;
	}

	public static MemoryData of(Long memoryId) {
		return MemoryData.builder().type(MEMORY).memoryId(memoryId).build();
	}
}