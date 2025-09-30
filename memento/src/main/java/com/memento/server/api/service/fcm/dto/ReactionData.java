package com.memento.server.api.service.fcm.dto;

import static com.memento.server.domain.notification.NotificationType.REACTION;

import java.util.HashMap;
import java.util.Map;

import com.memento.server.domain.notification.NotificationType;

import lombok.Builder;

@Builder
public record ReactionData(
	NotificationType type,
	Long memoryId,
	Long postId
) implements FCMData {

	@Override
	public Map<String, String> toDataMap() {
		Map<String, String> data = new HashMap<>();
		data.put("type", type.name());
		data.put("memoryId", String.valueOf(memoryId));
		data.put("postId", String.valueOf(postId));
		return data;
	}

	public static ReactionData of(Long memoryId, Long postId) {
		return ReactionData.builder().type(REACTION).memoryId(memoryId).postId(postId).build();
	}
}