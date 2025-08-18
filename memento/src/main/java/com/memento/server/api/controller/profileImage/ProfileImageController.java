package com.memento.server.api.controller.profileImage;

import javax.annotation.Nullable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.memento.server.annotation.CommunityId;
import com.memento.server.api.controller.profileImage.dto.SearchProfileImageResponse;
import com.memento.server.api.service.profileImage.ProfileImageService;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/communities/{communityId}/associates/{associateId}/profile-images")
@RequiredArgsConstructor
public class ProfileImageController {

	private final ProfileImageService profileImageService;

	@PostMapping()
	public ResponseEntity<Void> create(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long associateId,
		@RequestPart @NotNull(message = "image는 null일 수 없습니다.") MultipartFile image
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}
		if(currentAssociateId.equals(associateId)) {
			throw new MementoException(ErrorCodes.ASSOCIATE_NOT_AUTHORITY);
		}

		profileImageService.create(communityId, associateId, currentAssociateId, image);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{profileImageId}")
	public ResponseEntity<Void> delete(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long associateId,
		@PathVariable Long profileImageId
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}

		profileImageService.delete(communityId, currentAssociateId, associateId, profileImageId);
		return ResponseEntity.ok().build();
	}

	@GetMapping()
	public ResponseEntity<SearchProfileImageResponse> search(
		@CommunityId Long currentCommunityId,
		@PathVariable Long communityId,
		@PathVariable Long associateId,
		@RequestParam(required = false, defaultValue = "10") int size,
		@RequestParam(required = false) Long cursor
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}

		Pageable pageable = PageRequest.of(0, size);
		return ResponseEntity.ok(profileImageService.search(communityId, associateId, pageable, cursor));
	}
}
