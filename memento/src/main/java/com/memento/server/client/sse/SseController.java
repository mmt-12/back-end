package com.memento.server.client.sse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.memento.server.annotation.AssociateId;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/sse")
@RequiredArgsConstructor
public class SseController {

	private final SseEmitterRepository sseEmitterRepository;

	@GetMapping(value = "/subscribe", produces = "text/event-stream")
	public SseEmitter subscribe(
		@AssociateId Long currentAssociateId
	) {
		SseEmitter emitter = new SseEmitter(0L);

		if(currentAssociateId == null){
			throw new MementoException(ErrorCodes.ASSOCIATE_NOT_AUTHORITY);
		}

		sseEmitterRepository.save(currentAssociateId, emitter);

		emitter.onCompletion(() -> sseEmitterRepository.remove(currentAssociateId));
		emitter.onTimeout(() -> sseEmitterRepository.remove(currentAssociateId));
		emitter.onError(e -> sseEmitterRepository.remove(currentAssociateId));

		return emitter;
	}
}
