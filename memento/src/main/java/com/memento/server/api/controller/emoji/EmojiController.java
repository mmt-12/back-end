package com.memento.server.api.controller.emoji;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.controller.emoji.dto.request.EmojiCreateRequest;
import com.memento.server.api.service.emoji.EmojiService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/groups/{groupId}/emoji")
public class EmojiController {

	private final EmojiService emojiService;

	@PostMapping
	public ResponseEntity<Void> createEmoji(@PathVariable("groupId") int groupId,
		@Valid @RequestPart("data") EmojiCreateRequest request,
		@NotNull(message = "emoji 값은 필수입니다.") @RequestPart("emoji") MultipartFile emoji) {
		emojiService.createEmoji(request.toServiceRequest(emoji));
		return ResponseEntity.status(CREATED).build();
	}
}