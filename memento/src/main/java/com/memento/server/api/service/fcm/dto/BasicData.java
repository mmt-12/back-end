package com.memento.server.api.service.fcm.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;

@Builder
public record BasicData(
	String type
) implements FCMData {

	@Override
	public Map<String, String> toDataMap() {
		Map<String, String> data = new HashMap<>();
		data.put("type", type);
		return data;
	}
}