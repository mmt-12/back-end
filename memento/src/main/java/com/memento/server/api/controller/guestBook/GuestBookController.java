package com.memento.server.api.controller.guestBook;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.controller.guestBook.dto.CreateGuestBookRequest;
import com.memento.server.api.controller.guestBook.dto.ReadGuestBookResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/communities/{communityId}/associates/{associateId}/guest-books")
@RequiredArgsConstructor
public class GuestBookController {

	@PostMapping()
	public ResponseEntity<Void> create(
		@PathVariable Long associateId,
		@RequestBody CreateGuestBookRequest request
	) {
		return ResponseEntity.ok().build();
	}

	@PostMapping("/bubble")
	public ResponseEntity<Void> creatBubble(
		@PathVariable Long associateId,
		@RequestPart MultipartFile voice
	) {
		return ResponseEntity.ok().build();
	}

	@GetMapping
	public ResponseEntity<ReadGuestBookResponse> read(
		@PathVariable Long associateId,
		@RequestParam(required = false, defaultValue = "10") Long size,
		@RequestParam(required = false) Long cursor
	) {
		return ResponseEntity.ok(ReadGuestBookResponse.from());
	}

	@DeleteMapping("/{guestBookId}")
	public ResponseEntity<Void> delete(
		@PathVariable Long associateId,
		@PathVariable Long guestBookId
	){
		return ResponseEntity.ok().build();
	}
}
