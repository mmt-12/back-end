package com.memento.server.api.controller.guestBook;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.memento.server.annotation.AssociateId;
import com.memento.server.annotation.CommunityId;
import com.memento.server.api.controller.guestBook.dto.CreateGuestBookRequest;
import com.memento.server.api.controller.guestBook.dto.SearchGuestBookResponse;
import com.memento.server.api.service.guestBook.GuestBookService;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/communities/{communityId}/associates/{associateId}/guest-books")
@RequiredArgsConstructor
public class GuestBookController {

	private final GuestBookService guestBookService;

	@PostMapping()
	public ResponseEntity<Void> create(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long associateId,
		@Valid @RequestBody CreateGuestBookRequest request
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}
		if(currentAssociateId.equals(associateId)) {
			throw new MementoException(ErrorCodes.ASSOCIATE_NOT_AUTHORITY);
		}

		guestBookService.create(communityId, associateId, request.type(), request.contentId(), request.content());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/bubble")
	public ResponseEntity<Void> creatBubble(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long associateId,
		@RequestPart MultipartFile voice
	) {
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}
		if(currentAssociateId.equals(associateId)) {
			throw new MementoException(ErrorCodes.ASSOCIATE_NOT_AUTHORITY);
		}

		guestBookService.createBubble(communityId, associateId, voice);
		return ResponseEntity.ok().build();
	}

	@GetMapping
	public ResponseEntity<SearchGuestBookResponse> search(
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
		return ResponseEntity.ok(guestBookService.search(communityId, associateId, pageable, cursor));
	}

	@DeleteMapping("/{guestBookId}")
	public ResponseEntity<Void> delete(
		@CommunityId Long currentCommunityId,
		@AssociateId Long currentAssociateId,
		@PathVariable Long communityId,
		@PathVariable Long associateId,
		@PathVariable Long guestBookId
	){
		if (!currentCommunityId.equals(communityId)) {
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}
		if(!currentAssociateId.equals(associateId)) {
			throw new MementoException(ErrorCodes.ASSOCIATE_NOT_AUTHORITY);
		}

		guestBookService.delete(guestBookId);
		return ResponseEntity.ok().build();
	}
}
