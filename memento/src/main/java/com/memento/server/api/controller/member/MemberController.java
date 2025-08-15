package com.memento.server.api.controller.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.annotation.MemberId;
import com.memento.server.api.controller.member.dto.CommunityListResponse;
import com.memento.server.api.controller.member.dto.MemberSignUpRequest;
import com.memento.server.api.controller.member.dto.MemberSignUpResponse;
import com.memento.server.api.controller.member.dto.MemberUpdateRequest;
import com.memento.server.api.service.community.AssociateService;
import com.memento.server.api.service.member.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

	private final MemberService memberService;
	private final AssociateService associateService;

	@PostMapping
	public ResponseEntity<MemberSignUpResponse> signUp(@MemberId Long kakaoId,
		@RequestBody MemberSignUpRequest request) {
		return ResponseEntity.ok(
			memberService.signUp(kakaoId, request.name(), request.email(), request.birthday()));
	}

	@PutMapping
	public ResponseEntity<Void> update(@MemberId Long memberId, @RequestBody MemberUpdateRequest request) {
		memberService.update(memberId, request.name(), request.email());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/associates")
	public ResponseEntity<CommunityListResponse> searchAllAssociate(@MemberId Long memberId) {
		return ResponseEntity.ok(associateService.searchAllMyAssociate(memberId));
	}
}
