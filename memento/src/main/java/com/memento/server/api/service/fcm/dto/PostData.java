package com.memento.server.api.service.fcm.dto;

import static com.memento.server.domain.notification.NotificationType.POST;

import java.util.HashMap;
import java.util.Map;

import com.memento.server.domain.notification.NotificationType;

import lombok.Builder;

@Builder
public record PostData(
	NotificationType type,
	Long postId,
	Long memoryId
) implements FCMData {

	@Override
	public Map<String, String> toDataMap() {
		Map<String, String> data = new HashMap<>();
		data.put("type", type.name());
		data.put("postId", String.valueOf(postId));
		data.put("memoryId", String.valueOf(memoryId));
		return data;
	}

	public static PostData of(Long postId, Long memoryId) {
		return PostData.builder().type(POST).postId(postId).memoryId(memoryId).build();
	}
}