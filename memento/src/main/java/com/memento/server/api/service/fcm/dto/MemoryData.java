package com.memento.server.api.service.fcm.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;

@Builder
public record MemoryData(
	String type,
	Long memoryId
) implements FCMData {

	@Override
	public Map<String, String> toDataMap() {
		Map<String, String> data = new HashMap<>();
		data.put("type", type);
		if (memoryId != null) data.put("memoryId", String.valueOf(memoryId));
		return data;
	}
}