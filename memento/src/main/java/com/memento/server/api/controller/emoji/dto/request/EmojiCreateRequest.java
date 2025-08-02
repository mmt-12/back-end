package com.memento.server.api.controller.emoji.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.service.emoji.dto.request.EmojiCreateServiceRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record EmojiCreateRequest(
	@NotBlank(message = "name 값은 필수입니다.")
	@Size(max = 34, message = "name은 최대 34자(한글 기준)까지 입력 가능합니다.")
	String name
) {

	public EmojiCreateServiceRequest toServiceRequest(MultipartFile emoji) {
		return EmojiCreateServiceRequest.builder()
			.name(name)
			.emoji(emoji)
			.build();
	}
}
