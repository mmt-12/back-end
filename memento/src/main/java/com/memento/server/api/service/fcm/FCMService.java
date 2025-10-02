package com.memento.server.api.service.fcm;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.FCMTOKEN_DUPLICATE;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.memento.server.api.service.fcm.dto.request.FCMRequest;
import com.memento.server.api.service.fcm.dto.request.ReceiverInfo;
import com.memento.server.api.service.fcm.dto.request.SaveFCMTokenServiceRequest;
import com.memento.server.client.fcm.FCMSender;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.fcm.FCMToken;
import com.memento.server.domain.fcm.FCMTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FCMService {

	private final FCMSender sender;
	private final FCMTokenRepository fcmTokenRepository;
	private final AssociateRepository associateRepository;

	public void sendToAssociates(FCMRequest request) {
		Map<Long, ReceiverInfo> receiverMap = request.receiverInfos().stream()
			.collect(Collectors.toMap(ReceiverInfo::id, Function.identity()));
		List<FCMToken> fcmTokens = fcmTokenRepository.findAllByAssociateIds(List.copyOf(receiverMap.keySet()));

		for (FCMToken fcmToken : fcmTokens) {
			Long associateId = fcmToken.getAssociate().getId();
			ReceiverInfo receiverInfo = receiverMap.get(associateId);

			try {
				sender.send(fcmToken.getToken(), request.title(), receiverInfo.content(), request.dataDto().toDataMap());
			} catch (FirebaseMessagingException e) {
				if (isTokenInvalid(e)) {
					log.info("Deleting invalid FCM token: {}", fcmToken.getToken().substring(0, 10) + "...");
					fcmTokenRepository.deleteByToken(fcmToken.getToken());
				} else {
					log.error("FCM 전송 실패 - Token: {}, ErrorCode: {}, MessagingErrorCode: {}, Message: {}",
						fcmToken.getToken().substring(0, 10) + "...",
						e.getErrorCode(),
						e.getMessagingErrorCode(),
						e.getMessage());
				}
			}
		}
	}

	public void saveFCMToken(SaveFCMTokenServiceRequest request) {
		Associate associate = associateRepository.findByIdAndDeletedAtIsNull(request.associateId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_FOUND));
		FCMToken fcmToken = FCMToken.create(request.token(), associate);

		try {
			fcmTokenRepository.save(fcmToken);
		} catch (DataIntegrityViolationException e) {
			throw new MementoException(FCMTOKEN_DUPLICATE);
		}
	}

	private boolean isTokenInvalid(FirebaseMessagingException e) {
		return e.getMessagingErrorCode() != null &&
			(e.getMessagingErrorCode().name().equals("UNREGISTERED") ||
			 e.getMessagingErrorCode().name().equals("INVALID_ARGUMENT"));
	}
}
