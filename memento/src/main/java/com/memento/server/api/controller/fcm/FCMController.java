package com.memento.server.api.controller.fcm;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.annotation.AssociateId;
import com.memento.server.api.controller.fcm.request.SaveFCMTokenRequest;
import com.memento.server.api.service.fcm.FCMService;
import com.memento.server.api.service.fcm.dto.request.SaveFCMTokenServiceRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FCMController {

	private final FCMService fcmService;

	@PostMapping
	public ResponseEntity<Void> saveFCMToken(@AssociateId Long associateId,
		@Valid @RequestBody SaveFCMTokenRequest request) {
		fcmService.saveFCMToken(SaveFCMTokenServiceRequest.of(associateId, request.token()));
		return ResponseEntity.status(CREATED).build();
	}
}
