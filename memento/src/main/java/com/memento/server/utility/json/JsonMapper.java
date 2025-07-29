package com.memento.server.utility.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapper {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static <T> T readValue(String string, Class<T> type) {
		try {
			return objectMapper.readValue(string, type);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
