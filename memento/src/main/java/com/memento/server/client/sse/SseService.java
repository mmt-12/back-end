package com.memento.server.client.sse;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
}