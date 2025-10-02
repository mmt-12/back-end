package com.memento.server.api.service.fcm.dto.request;

import java.util.HashMap;
import java.util.Map;

import com.memento.server.domain.notification.NotificationType;

import lombok.Builder;

@Builder
public record BasicData(
	NotificationType type
) implements FCMData {

	@Override
	public Map<String, String> toDataMap() {
		Map<String, String> data = new HashMap<>();
		data.put("type", type.name());
		return data;
	}

	public static BasicData of(NotificationType type) {
		return BasicData.builder().type(type).build();
	}
}