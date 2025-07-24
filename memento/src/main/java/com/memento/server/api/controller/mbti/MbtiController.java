package com.memento.server.api.controller.mbti;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.api.controller.mbti.dto.CreateMbtiRequest;
import com.memento.server.api.controller.mbti.dto.ReadMbtiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/associates/{associateId}/mbti-tests")
@RequiredArgsConstructor
public class MbtiController {

	@PostMapping()
	public ResponseEntity<Void> create(
		@PathVariable Long associateId,
		@RequestBody CreateMbtiRequest request
	) {
		return ResponseEntity.ok().build();
	}

	@GetMapping()
	public ResponseEntity<ReadMbtiResponse> read(
		@PathVariable Long associateId
	) {
		return ResponseEntity.ok(ReadMbtiResponse.from());
	}
}
