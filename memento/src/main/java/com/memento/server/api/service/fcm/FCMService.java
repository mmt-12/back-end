package com.memento.server.api.service.fcm;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.domain.fcm.FCMTokenRepository;
import com.memento.server.domain.notification.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FCMService {

	private final NotificationRepository notificationRepository;
	private final FCMTokenRepository fcmTokenRepository;


}
