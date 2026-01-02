package com.memento.server.api.controller.member;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.memento.server.annotation.MemberId;
import com.memento.server.api.controller.auth.dto.AuthResponse;
import com.memento.server.api.controller.member.dto.CommunityListResponse;
import com.memento.server.api.controller.member.dto.MemberNormalSignUpRequest;
import com.memento.server.api.controller.member.dto.MemberSignUpRequest;
import com.memento.server.api.controller.member.dto.MemberSignUpResponse;
import com.memento.server.api.controller.member.dto.MemberSignUpResultRequest;
import com.memento.server.api.controller.member.dto.MemberUpdateRequest;
import com.memento.server.api.controller.member.dto.SignInRequest;
import com.memento.server.api.service.community.AssociateService;
import com.memento.server.api.service.member.MemberService;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

	private final MemberService memberService;
	private final AssociateService associateService;

	@GetMapping("/check-email")
	public ResponseEntity<Void> checkDuplicateEmail(
		@RequestParam("email")
		@NotBlank(message = "이메일은 필수입니다.")
		@Email(
			message = "올바른 이메일 형식이 아닙니다.",
			regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
		)
		String email
	) {
    memberService.checkDuplicateEmail(email);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/signup/kakao")
	public ResponseEntity<MemberSignUpResponse> signUp(@MemberId Long kakaoId,
		@RequestBody MemberSignUpRequest request) {
		return ResponseEntity.ok(
			memberService.signUp(kakaoId, request));
	}

	@PostMapping("/signup/normal")
	public ResponseEntity<Void> normalSignUp(@RequestBody MemberNormalSignUpRequest request) {
		memberService.normalSignUp(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/signup/result")
	public ResponseEntity<Void> signUpResult(@RequestBody MemberSignUpResultRequest request) {
		memberService.signUpResult(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> singIn(@RequestBody SignInRequest request) {
		return ResponseEntity.ok(memberService.signIn(request));
	}

	@PutMapping
	public ResponseEntity<Void> update(@MemberId Long memberId, @RequestBody MemberUpdateRequest request) {
		memberService.update(memberId, request.name(), request.email());
		return ResponseEntity.ok().build();
	}

	// todo 지금 안씀
	@GetMapping("/associates")
	public ResponseEntity<CommunityListResponse> searchAllAssociate(@MemberId Long memberId) {
		return ResponseEntity.ok(associateService.searchAllMyAssociate(memberId));
	}
}
