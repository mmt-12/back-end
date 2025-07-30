package com.memento.server.api.controller.voice;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.api.controller.voice.dto.request.VoiceCreateRequest;
import com.memento.server.api.service.voice.VoiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/groups/{groupId}/voices")
public class VoiceController {

	private final VoiceService voiceService;

	@PostMapping
	public ResponseEntity<Void> createVoice(@PathVariable Long groupId,
		@Valid @RequestPart VoiceCreateRequest request) {
		voiceService.createVoice(request.toServiceRequest());
		return ResponseEntity.status(CREATED).build();
	}
}
