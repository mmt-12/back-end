package com.memento.server.api.service.fcm.dto.request;

import static com.memento.server.domain.notification.NotificationType.BIRTHDAY;

import java.util.HashMap;
import java.util.Map;

import com.memento.server.domain.notification.NotificationType;

import lombok.Builder;

@Builder
public record BirthdayData(
	NotificationType type,
	Long associateId
) implements FCMData {

	@Override
	public Map<String, String> toDataMap() {
		Map<String, String> data = new HashMap<>();
		data.put("type", type.name());
		data.put("associateId", String.valueOf(associateId));
		return data;
	}

	public static BirthdayData of(Long associateId) {
		return BirthdayData.builder().type(BIRTHDAY).associateId(associateId).build();
	}
}
