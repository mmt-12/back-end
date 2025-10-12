package com.memento.server.api.controller.health.dto;

import lombok.Builder;

@Builder
public record HealthResponse(
	String status
)  {
}