package com.memento.server.api.service.fcm;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.memento.server.api.service.fcm.dto.FCMRequest;
import com.memento.server.client.fcm.FCMSender;
import com.memento.server.domain.fcm.FCMToken;
import com.memento.server.domain.fcm.FCMTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = REQUIRES_NEW)
public class FCMService {

	private final FCMSender sender;
	private final FCMTokenRepository fcmTokenRepository;

	public void sendToAssociates(FCMRequest request) {
		List<FCMToken> fcmTokens = fcmTokenRepository.findAllByAssociateIds(request.receiverIds());

		for (FCMToken fcmToken : fcmTokens) {
			try {
				sender.send(fcmToken.getToken(), request.title(), request.content(), request.dataDto().toDataMap());
			} catch (FirebaseMessagingException e) {
				if (isTokenInvalid(e)) {
					log.info("Deleting invalid FCM token: {}", fcmToken.getToken().substring(0, 10) + "...");
					fcmTokenRepository.deleteByToken(fcmToken.getToken());
				} else {
					throw new RuntimeException("FCM 전송 실패", e);
				}
			}
		}
	}

	private boolean isTokenInvalid(FirebaseMessagingException e) {
		return e.getMessagingErrorCode() != null &&
			(e.getMessagingErrorCode().name().equals("UNREGISTERED") ||
			 e.getMessagingErrorCode().name().equals("INVALID_ARGUMENT"));
	}
}
