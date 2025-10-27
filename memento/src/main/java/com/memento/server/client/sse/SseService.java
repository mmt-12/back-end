package com.memento.server.client.sse;

import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.memento.server.domain.achievement.Achievement;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SseService {

	private final SseEmitterRepository sseEmitterRepository;

	// 30초마다 실행
	@Scheduled(fixedRate = 30000)
	public void sendKeepAlives() {
		sseEmitterRepository.getAllEmitters().forEach((associateId, emitter) -> {
			try {
				emitter.send(SseEmitter.event().comment("sse 연결 유지"));
			} catch (Exception e) {
				sseEmitterRepository.remove(associateId);
			}
		});
	}

	public void sendAchievementSse(Long associateId, Achievement achievement) {
		SseEmitter emitter = sseEmitterRepository.get(associateId);
		if (emitter == null) return;

		try {
			Map<String, Object> data = Map.of(
				"type", "ACHIEVE",
				"value", Map.of(
					"id", achievement.getId(),
					"name", achievement.getName(),
					"criteria", achievement.getCriteria(),
					"type", achievement.getType().name()
				)
			);

			emitter.send(SseEmitter.event()
				.name("message")
				.data(data)
			);
		} catch (Exception e) {
			sseEmitterRepository.remove(associateId);
		}
	}
}