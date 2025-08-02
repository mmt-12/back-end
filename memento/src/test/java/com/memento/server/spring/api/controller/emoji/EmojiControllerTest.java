package com.memento.server.spring.api.controller.emoji;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.emoji.dto.request.EmojiCreateRequest;
import com.memento.server.spring.ControllerTestSupport;

public class EmojiControllerTest extends ControllerTestSupport {

	@Test
	@DisplayName("이모지 리액션을 등록한다.")
	void createEmoji() throws Exception {
		// given
		long groupId = 1L;

		String json = objectMapper.writeValueAsString(EmojiCreateRequest.builder()
			.name("인쥐용")
			.build());

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"request",
			"application/json",
			json.getBytes()
		);

		MockMultipartFile emoji = new MockMultipartFile(
			"emoji",
			"emoji.png",
			"image/png",
			new byte[] {(byte) 0x89, 'P', 'N', 'G'}
		);

		// when & then
		mockMvc.perform(
				multipart("/api/v1/groups/{groupId}/emoji", groupId)
					.file(data)
					.file(emoji)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("이모지를 등록할 때, name은 필수값이다.")
	void createEmojiWithoutName() throws Exception {
		// given
		long groupId = 1L;

		String json = objectMapper.writeValueAsString(EmojiCreateRequest.builder().build());

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"request",
			"application/json",
			json.getBytes()
		);

		MockMultipartFile emoji = new MockMultipartFile(
			"emoji",
			"emoji.png",
			"image/png",
			new byte[] {(byte) 0x89, 'P', 'N', 'G'}
		);

		// when & then
		mockMvc.perform(
				multipart("/api/v1/groups/{groupId}/emoji", groupId)
					.file(data)
					.file(emoji)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("name"))
			.andExpect(jsonPath("$.errors[0].message").value("name 값은 필수입니다."));
	}

	@Test
	@DisplayName("이모지를 등록할 때, name은 최대 34자(한글 기준)까지 입력 가능하다.")
	void createEmojiWithTooLongName() throws Exception {
		// given
		long groupId = 1L;

		String tooLongName = "가".repeat(35);

		String json = objectMapper.writeValueAsString(EmojiCreateRequest.builder().name(tooLongName).build());

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"request",
			"application/json",
			json.getBytes()
		);

		MockMultipartFile emoji = new MockMultipartFile(
			"emoji",
			"emoji.png",
			"image/png",
			new byte[] {(byte) 0x89, 'P', 'N', 'G'}
		);

		// when & then
		mockMvc.perform(
				multipart("/api/v1/groups/{groupId}/emoji", groupId)
					.file(data)
					.file(emoji)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("name"))
			.andExpect(jsonPath("$.errors[0].message").value("name은 최대 34자(한글 기준)까지 입력 가능합니다."));
	}

	@Test
	@DisplayName("이모지를 등록할 때, data는 필수값이다.")
	void createEmojiWithoutData() throws Exception {
		// given
		long groupId = 1L;

		MockMultipartFile emoji = new MockMultipartFile(
			"emoji",
			"emoji.png",
			"image/png",
			new byte[] {(byte) 0x89, 'P', 'N', 'G'}
		);

		// when & then
		mockMvc.perform(
				multipart("/api/v1/groups/{groupId}/emoji", groupId)
					.file(emoji)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1002))
			.andExpect(jsonPath("$.message").value("필수 요청 part 누락"))
			.andExpect(jsonPath("$.errors[0].field").value("data"))
			.andExpect(jsonPath("$.errors[0].message").value("data은(는) 필수입니다."));
	}

	@Test
	@DisplayName("이모지를 등록할 때, emoji는 필수값이다.")
	void createEmojiWithoutEmoji() throws Exception {
		// given
		long groupId = 1L;

		String json = objectMapper.writeValueAsString(EmojiCreateRequest.builder()
			.name("인쥐용")
			.build());


		MockMultipartFile data = new MockMultipartFile(
			"data",
			"request",
			"application/json",
			json.getBytes()
		);

		// when & then
		mockMvc.perform(
				multipart("/api/v1/groups/{groupId}/emoji", groupId)
					.file(data)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1002))
			.andExpect(jsonPath("$.message").value("필수 요청 part 누락"))
			.andExpect(jsonPath("$.errors[0].field").value("emoji"))
			.andExpect(jsonPath("$.errors[0].message").value("emoji은(는) 필수입니다."));
	}
}
