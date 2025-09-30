package com.memento.server.api.service.fcm.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;

@Builder
public record PostData(
	String type,
	Long postId,
	Long memoryId
) implements FCMData {

	@Override
	public Map<String, String> toDataMap() {
		Map<String, String> data = new HashMap<>();
		data.put("type", type);
		if (postId != null) data.put("postId", String.valueOf(postId));
		if (memoryId != null) data.put("memoryId", String.valueOf(memoryId));
		return data;
	}
}