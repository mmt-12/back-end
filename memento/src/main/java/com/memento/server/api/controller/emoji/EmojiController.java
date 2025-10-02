package com.memento.server.api.controller.emoji;

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
import com.memento.server.api.controller.emoji.dto.request.EmojiCreateRequest;
import com.memento.server.api.service.emoji.EmojiService;
import com.memento.server.api.service.emoji.dto.request.EmojiListQueryRequest;
import com.memento.server.api.service.emoji.dto.request.EmojiRemoveRequest;
import com.memento.server.api.service.emoji.dto.response.EmojiListResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/communities/{communityId}/emojis")
public class EmojiController {

	private final EmojiService emojiService;

	@PostMapping
	public ResponseEntity<Void> createEmoji(@AssociateId Long associateId,
		@PathVariable("communityId") Long communityId,
		@Valid @RequestPart("data") EmojiCreateRequest request,
		@NotNull(message = "emoji 값은 필수입니다.") @RequestPart("emoji") MultipartFile emoji) {
		emojiService.createEmoji(request.toServiceRequest(associateId, emoji));
		return ResponseEntity.status(CREATED).build();
	}

	@GetMapping
	public ResponseEntity<EmojiListResponse> getEmoji(@PathVariable("communityId") Long communityId,
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false, defaultValue = "10") int size,
		@RequestParam(required = false) String keyword) {
		return ResponseEntity.ok(emojiService.getEmoji(EmojiListQueryRequest.of(communityId, cursor, size, keyword)));
	}

	@DeleteMapping("/{emojiId}")
	public ResponseEntity<Void> removeEmoji(@AssociateId Long associateId, @PathVariable("emojiId") Long emojiId) {
		emojiService.removeEmoji(EmojiRemoveRequest.of(associateId, emojiId));
		return ResponseEntity.noContent().build();
	}
}