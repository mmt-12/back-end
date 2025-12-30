package com.memento.server.api.controller.member.dto;

public record SignInRequest(
	String email,
	String password
) {
}
