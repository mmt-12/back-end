package com.memento.server.client.fcm;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Component
public class FCMSender {

	public void send(final String token, final String title, final String content, final Map<String, String> data)
		throws FirebaseMessagingException {

		Notification notification = Notification.builder()
			.setTitle(title)
			.setBody(content)
			.build();

		Message message = Message.builder()
			.setToken(token)
			.setNotification(notification)
			.putAllData(data)
			.build();

		FirebaseMessaging.getInstance().send(message);
	}
}
