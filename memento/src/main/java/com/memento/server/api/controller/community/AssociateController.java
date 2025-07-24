package com.memento.server.api.controller.community;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.api.controller.community.dto.ReadAssociateResponse;
import com.memento.server.api.controller.community.dto.UpdateAssociateRequest;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/associates")
public class AssociateController {

	@GetMapping("/{associateId}")
	public ResponseEntity<ReadAssociateResponse> read(
		@PathVariable Long associateId
	) {
		return ResponseEntity.ok(ReadAssociateResponse.from());
	}

	@PutMapping()
	public ResponseEntity<Void> update(
		UpdateAssociateRequest request
	) {
		return ResponseEntity.ok().build();
	}
}
