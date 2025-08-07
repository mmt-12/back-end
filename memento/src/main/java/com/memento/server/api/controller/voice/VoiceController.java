package com.memento.server.api.controller.voice;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.annotation.AssociateId;
import com.memento.server.api.controller.voice.dto.request.VoiceCreateRequest;
import com.memento.server.api.service.voice.VoiceService;
import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.memento.server.api.service.voice.dto.request.VoiceRemoveRequest;
import com.memento.server.api.service.voice.dto.response.VoiceListResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/communities/{communityId}/voices")
public class VoiceController {

	private final VoiceService voiceService;

	@PostMapping
	public ResponseEntity<Void> createVoice(@AssociateId Long associateId,
		@PathVariable("communityId") Long communityId,
		@Valid @RequestPart("data") VoiceCreateRequest request,
		@NotNull(message = "voice 값은 필수입니다.") @RequestPart("voice") MultipartFile voice) {
		voiceService.createVoice(request.toServiceRequest(associateId, voice));
		return ResponseEntity.status(CREATED).build();
	}

	@GetMapping
	public ResponseEntity<VoiceListResponse> getVoices(@PathVariable("communityId") Long communityId,
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false, defaultValue = "10") int size,
		@RequestParam(required = false) String keyword) {
		return ResponseEntity.ok(voiceService.getVoices(VoiceListQueryRequest.of(communityId, cursor, size, keyword)));
	}

	@DeleteMapping("/{voiceId}")
	public ResponseEntity<Void> removeVoice(@AssociateId Long associateId, @PathVariable("voiceId") Long voiceId) {
		voiceService.removeVoice(VoiceRemoveRequest.of(associateId, voiceId));
		return ResponseEntity.noContent().build();
	}
}
