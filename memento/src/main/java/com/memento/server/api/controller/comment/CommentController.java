package com.memento.server.api.controller.comment;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.annotation.AssociateId;
import com.memento.server.api.controller.comment.dto.EmojiCommentCreateRequest;
import com.memento.server.api.controller.comment.dto.VoiceCommentCreateRequest;
import com.memento.server.api.service.comment.CommentService;
import com.memento.server.api.service.comment.dto.request.CommentDeleteServiceRequest;
import com.memento.server.api.service.comment.dto.request.VoiceCommentCreateServiceRequest;
import com.memento.server.api.service.voice.VoiceService;
import com.memento.server.api.service.voice.dto.request.TemporaryVoiceCreateServiceRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/communities/{communityId}/memories/{memoryId}/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;
	private final VoiceService voiceService;

	@PostMapping("/emoji")
	public ResponseEntity<Void> createEmojiComment(@PathVariable("postId") Long postId,
		@PathVariable("communityId") Long communityId,
		@Valid @RequestBody EmojiCommentCreateRequest request,
		@AssociateId Long associateId) {
		commentService.createEmojiComment(request.toServiceRequest(postId, associateId));
		return ResponseEntity.status(CREATED).build();
	}

	@PostMapping("/voices")
	public ResponseEntity<Void> createVoiceComment(@PathVariable("postId") Long postId,
		@PathVariable("communityId") Long communityId,
		@Valid @RequestBody VoiceCommentCreateRequest request,
		@AssociateId Long associateId) {
		commentService.createVoiceComment(request.toServiceRequest(postId, associateId));
		return ResponseEntity.status(CREATED).build();
	}

	@PostMapping("/bubble")
	public ResponseEntity<Void> createTemporaryVoiceComment(@PathVariable("postId") Long postId,
		@PathVariable("communityId") Long communityId,
		@NotNull(message = "voice 값은 필수입니다.") @RequestPart("voice") MultipartFile voice,
		@AssociateId Long associateId) {
		final Long voiceId = voiceService.createTemporaryVoice(
			TemporaryVoiceCreateServiceRequest.of(associateId, voice));
		commentService.createVoiceComment(VoiceCommentCreateServiceRequest.of(voiceId, postId, associateId));
		return ResponseEntity.status(CREATED).build();
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId,
		@AssociateId Long associateId) {
		commentService.deleteComment(CommentDeleteServiceRequest.of(commentId, associateId));
		return ResponseEntity.noContent().build();
	}
}
