package com.memento.server.api.service.fcm.dto;

import static com.memento.server.domain.notification.NotificationType.ASSOCIATE;

import java.util.HashMap;
import java.util.Map;

import com.memento.server.domain.notification.NotificationType;

import lombok.Builder;

@Builder
public record AssociateData(
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

	public static AssociateData of(Long associateId) {
		return AssociateData.builder().type(ASSOCIATE).associateId(associateId).build();
	}
}
