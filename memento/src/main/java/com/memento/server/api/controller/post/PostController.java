package com.memento.server.api.controller.post;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.controller.post.dto.CreatePostRequest;
import com.memento.server.api.controller.post.dto.ReadAllPostResponse;
import com.memento.server.api.controller.post.dto.ReadPostResponse;
import com.memento.server.api.controller.post.dto.UpdatePostRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/communities/{communityId}/memories/{memoryId}/posts")
@RequiredArgsConstructor
public class PostController {

	@GetMapping("/{postId}")
	public ResponseEntity<ReadPostResponse> read(
		@PathVariable Long postId
	){
		return ResponseEntity.ok(ReadPostResponse.from());
	}

	@GetMapping()
	public ResponseEntity<ReadAllPostResponse> readAll(
		@PathVariable Long memoryId
	){
		return ResponseEntity.ok(ReadAllPostResponse.from());
	}

	@PostMapping()
	public ResponseEntity<Void> create(
		@PathVariable Long memoryId,
		@RequestPart CreatePostRequest request,
		@RequestPart(required = false) List<MultipartFile> pictures
	){
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{postId}")
	public ResponseEntity<Void> update(
		@PathVariable Long postId,
		@RequestPart UpdatePostRequest request,
		@RequestPart(required = false) List<MultipartFile> nswPictures
	) {
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{postId}")
	public ResponseEntity<Void> delete(
		@PathVariable Long postId
	) {
		return ResponseEntity.ok().build();
	}
}
