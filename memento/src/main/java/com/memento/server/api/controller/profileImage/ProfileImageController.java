package com.memento.server.api.controller.profileImage;

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

import com.memento.server.api.controller.profileImage.dto.ReadProfileImageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/associates/{associateId}/profile-images")
@RequiredArgsConstructor
public class ProfileImageController {

	@PostMapping()
	public ResponseEntity<Void> create(
		@PathVariable Long associateId,
		@RequestPart MultipartFile image
	) {
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{profileImageId}")
	public ResponseEntity<Void> delete(
		@PathVariable Long profileImageId
	) {
		return ResponseEntity.ok().build();
	}

	@GetMapping()
	public ResponseEntity<ReadProfileImageResponse> read(
		@PathVariable Long groupId,
		@PathVariable Long associateId,
		@RequestParam(required = false, defaultValue = "10") Long size,
		@RequestParam(required = false) Long cursor
	) {
		return ResponseEntity.ok(ReadProfileImageResponse.from());
	}
}
