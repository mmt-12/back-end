package com.memento.server.api.service.fcm.dto.request;

import lombok.Builder;

@Builder
public record SaveFCMTokenServiceRequest(
	Long associateId,
	String token
) {

	public static SaveFCMTokenServiceRequest of(Long associateId, String token) {
		return SaveFCMTokenServiceRequest.builder().associateId(associateId).token(token).build();
	}
}
