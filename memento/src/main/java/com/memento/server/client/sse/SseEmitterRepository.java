package com.memento.server.client.sse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmitterRepository {
	private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

	public void save(Long associateId, SseEmitter emitter){
		emitters.put(associateId, emitter);
	}

	public SseEmitter get(Long associateId){
		return emitters.get(associateId);
	}

	public Map<Long, SseEmitter> getAllEmitters() {
		return emitters;
	}

	public void remove(Long associateId){
		emitters.remove(associateId);
	}
}
