package com.memento.server.api.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.memento.server.api.controller.auth.dto.AuthResponse;
import com.memento.server.api.service.auth.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@GetMapping("/api/v1/sign-in")
	public RedirectView signIn() {
		return new RedirectView(authService.getAuthUrl());
	}

	@GetMapping("/api/v1/auth/redirect")
	public ResponseEntity<AuthResponse> handleRedirect(@RequestParam String code) {
		return ResponseEntity.ok(authService.handleAuthorizationCallback(code));
	}
}
