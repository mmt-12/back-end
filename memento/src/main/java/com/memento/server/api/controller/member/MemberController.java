package com.memento.server.api.controller.member;

import static org.springframework.http.MediaType.*;

import com.memento.server.api.controller.member.dto.EmailCheckResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
    private final TemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping("/check-email")
    public ResponseEntity<EmailCheckResponse> checkDuplicateEmail(
            @RequestParam("email")
            @NotBlank(message = "이메일은 필수입니다.")
            @Email(
                    message = "올바른 이메일 형식이 아닙니다.",
                    regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
            )
            String email
    ) {
        return ResponseEntity.ok(memberService.checkDuplicateEmail(email));
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

    @GetMapping("/signup/page")
    public ResponseEntity<String> signUpPage(
            @RequestParam("memberId") Long memberId,
            @RequestParam("action") String action,
            @RequestParam("token") String token
    ) {
        return ResponseEntity.ok().contentType(TEXT_HTML)
                .body(templateEngine.process("email/signup-result-page", getContext(memberId, action, token)));
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

    private Context getContext(Long memberId, String action, String token) {
        Context context = new Context();
        context.setVariable("memberId", memberId);
        context.setVariable("token", token);
        context.setVariable("action", action);
        context.setVariable("baseUrl", baseUrl);
        return context;
    }
}
