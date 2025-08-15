package com.memento.server.notification;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.domain.post.Post;
import com.memento.server.post.PostFixtures;
import com.memento.server.memory.MemoryFixtures;
import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationType;

public class NotificationFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();

	public static Notification notificationWithType(NotificationType type) {
		return switch (type) {
			case MEMORY -> 	Notification.builder()
				.id(idGenerator.incrementAndGet())
				.title(type.getTitle())
				.content(type.getTitle())
				.type(type)
				.memoryId(MemoryFixtures.memory().getId())
				.receiver(AssociateFixtures.associate())
				.build();

			case REACTION -> Notification.builder()
				.id(idGenerator.incrementAndGet())
				.title(type.getTitle())
				.content(type.getTitle())
				.type(type)
				.postId(PostFixtures.post().getId())
				.receiver(AssociateFixtures.associate())
				.build();

			case POST -> {
				Post post = PostFixtures.post();
				yield Notification.builder()
				.id(idGenerator.incrementAndGet())
				.title(type.getTitle())
				.content(type.getTitle())
				.type(type)
				.postId(post.getId())
				.memoryId(post.getMemory().getId())
				.receiver(AssociateFixtures.associate())
				.build();
			}

			case ACHIEVE, GUESTBOOK, MBTI, NEWIMAGE -> Notification.builder()
				.id(idGenerator.incrementAndGet())
				.title(type.getTitle())
				.content(type.getTitle())
				.type(type)
				.receiver(AssociateFixtures.associate())
				.build();

			case BIRTHDAY, ASSOCIATE -> Notification.builder()
				.id(idGenerator.incrementAndGet())
				.title(type.getTitle())
				.content(type.getTitle())
				.type(type)
				.actorId(AssociateFixtures.associate().getId())
				.receiver(AssociateFixtures.associate())
				.build();
		};
	}
}
