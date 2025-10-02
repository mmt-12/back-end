package com.memento.server.api.controller.post;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.annotation.AssociateId;
import com.memento.server.annotation.CommunityId;
import com.memento.server.api.controller.post.dto.CreatePostRequest;
import com.memento.server.api.controller.post.dto.SearchAllPostResponse;
import com.memento.server.api.controller.post.dto.SearchPostResponse;
import com.memento.server.api.controller.post.dto.UpdatePostRequest;
import com.memento.server.api.service.post.PostService;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/communities/{communityId}/memories/{memoryId}/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@GetMapping("/{postId}")
	public ResponseEntity<SearchPostResponse> search(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long memoryId,
		@PathVariable Long postId
	){
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}
		return ResponseEntity.ok(postService.search(communityId, memoryId, currentAssociateId, postId));
	}

	@GetMapping()
	public ResponseEntity<SearchAllPostResponse> searchAll(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long memoryId,
		@RequestParam(required = false, defaultValue = "10") int size,
		@RequestParam(required = false) Long cursor
	){
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}
		return ResponseEntity.ok(postService.searchAll(communityId, memoryId, currentAssociateId, size, cursor));
	}

	@PostMapping()
	public ResponseEntity<Void> create(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long memoryId,
		@RequestPart @Valid CreatePostRequest request,
		@RequestPart(required = false) List<MultipartFile> pictures
	){
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}

		postService.create(communityId, memoryId, currentAssociateId, request.content(), pictures);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{postId}")
	public ResponseEntity<Void> update(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long postId,
		@PathVariable Long memoryId,
		@RequestPart @Valid UpdatePostRequest request,
		@RequestPart(required = false) List<MultipartFile> newPictures
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}

		postService.update(communityId, memoryId, currentAssociateId, postId, request.content(), request.oldPictures(), newPictures);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{postId}")
	public ResponseEntity<Void> delete(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long memoryId,
		@PathVariable Long postId
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}

		postService.delete(communityId, memoryId, currentAssociateId, postId);
		return ResponseEntity.ok().build();
	}
}
