package com.memento.server.controller.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.annotation.MemberId;
import com.memento.server.service.member.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

	private final MemberService memberService;

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
}
