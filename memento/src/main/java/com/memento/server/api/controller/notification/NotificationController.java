package com.memento.server.api.controller.notification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.annotation.AssociateId;
import com.memento.server.api.service.notification.NotificationService;
import com.memento.server.api.service.notification.dto.request.NotificationListQueryRequest;
import com.memento.server.api.service.notification.dto.response.NotificationListResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	public ResponseEntity<NotificationListResponse> getVoices(@AssociateId Long associateId,
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false, defaultValue = "10") int size) {
		return ResponseEntity.ok(
			notificationService.getNotifications(NotificationListQueryRequest.of(associateId, cursor, size)));
	}
}
