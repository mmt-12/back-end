package com.memento.server.spring.api.controller.emoji;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.emoji.dto.request.EmojiCreateRequest;
import com.memento.server.api.service.emoji.dto.request.EmojiListQueryRequest;
import com.memento.server.api.service.emoji.dto.request.EmojiRemoveRequest;
import com.memento.server.api.service.emoji.dto.response.EmojiListResponse;
import com.memento.server.api.service.emoji.dto.response.EmojiResponse;
import com.memento.server.common.dto.response.PageInfo;
import com.memento.server.common.fixture.CommonFixtures;
import com.memento.server.emoji.EmojiFixtures;
import com.memento.server.spring.api.controller.ControllerTestSupport;

public class EmojiControllerTest extends ControllerTestSupport {

	@Test
	@DisplayName("이모지 리액션을 생성한다.")
	void createEmoji() throws Exception {
		// given
		long communityId = 1L;
		MockMultipartFile data = CommonFixtures.jsonFile(EmojiCreateRequest.builder().name("인쥐용").build());
		MockMultipartFile emoji = CommonFixtures.emojiFile();

		doNothing().when(emojiService).createEmoji(any());

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/emoji", communityId)
					.file(data)
					.file(emoji)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated());

		verify(emojiService).createEmoji(any());
	}

	@Test
	@DisplayName("이모지 생성 시 name은 필수값이다.")
	void createEmojiWithoutName() throws Exception {
		// given
		long communityId = 1L;
		MockMultipartFile data = CommonFixtures.jsonFile(EmojiCreateRequest.builder().build());
		MockMultipartFile emoji = CommonFixtures.emojiFile();

		doNothing().when(emojiService).createEmoji(any());

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/emoji", communityId)
					.file(data)
					.file(emoji)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("name"))
			.andExpect(jsonPath("$.errors[0].message").value("name 값은 필수입니다."));

		verify(emojiService, never()).createEmoji(any());
	}

	@Test
	@DisplayName("이모지 생성 시 name은 최대 34자(한글 기준)까지 입력 가능하다.")
	void createEmojiWithTooLongName() throws Exception {
		// given
		long communityId = 1L;
		String tooLongName = "가".repeat(35);
		MockMultipartFile data = CommonFixtures.jsonFile(EmojiCreateRequest.builder().name(tooLongName).build());
		MockMultipartFile emoji = CommonFixtures.emojiFile();

		doNothing().when(emojiService).createEmoji(any());

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/emoji", communityId)
					.file(data)
					.file(emoji)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("name"))
			.andExpect(jsonPath("$.errors[0].message").value("name은 최대 34자(한글 기준)까지 입력 가능합니다."));

		verify(emojiService, never()).createEmoji(any());
	}

	@Test
	@DisplayName("이모지 생성 시 data는 필수값이다.")
	void createEmojiWithoutData() throws Exception {
		// given
		long communityId = 1L;
		MockMultipartFile emoji = CommonFixtures.emojiFile();

		doNothing().when(emojiService).createEmoji(any());

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/emoji", communityId)
					.file(emoji)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1002))
			.andExpect(jsonPath("$.message").value("필수 요청 part 누락"))
			.andExpect(jsonPath("$.errors[0].field").value("data"))
			.andExpect(jsonPath("$.errors[0].message").value("data은(는) 필수입니다."));

		verify(emojiService, never()).createEmoji(any());
	}

	@Test
	@DisplayName("이모지 생성 시 emoji는 필수값이다.")
	void createEmojiWithoutEmoji() throws Exception {
		// given
		long communityId = 1L;
		MockMultipartFile data = CommonFixtures.jsonFile(EmojiCreateRequest.builder().build());

		doNothing().when(emojiService).createEmoji(any());

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/emoji", communityId)
					.file(data)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1002))
			.andExpect(jsonPath("$.message").value("필수 요청 part 누락"))
			.andExpect(jsonPath("$.errors[0].field").value("emoji"))
			.andExpect(jsonPath("$.errors[0].message").value("emoji은(는) 필수입니다."));

		verify(emojiService, never()).createEmoji(any());
	}

	@Test
	@DisplayName("이모지 목록을 조회한다.")
	void getEmoji() throws Exception {
		// given
		long communityId = 1L;
		long cursor = 1L;
		String keyword = "인쥐용";
		int size = 10;
		Long nextCursor = cursor + size;
		boolean hasNext = true;

		EmojiResponse emojiResponse = EmojiResponse.of(EmojiFixtures.emoji());
		EmojiListResponse response = EmojiListResponse.of(List.of(emojiResponse), PageInfo.of(hasNext, nextCursor));

		given(emojiService.getEmoji(any(EmojiListQueryRequest.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(
				get("/api/v1/communities/{communityId}/emoji", communityId)
					.param("cursor", String.valueOf(cursor))
					.param("size", String.valueOf(size))
					.param("keyword", keyword)
					.with(withJwt(1L, 1L, 1L)))
			.andDo(print())
			.andExpect(status().isOk());

		verify(emojiService).getEmoji(any(EmojiListQueryRequest.class));
	}

	@Test
	@DisplayName("이모지를 삭제한다.")
	void removeEmoji() throws Exception {
		// given
		long communityId = 1L;
		long emojiId = 1L;

		doNothing().when(emojiService).removeEmoji(any(EmojiRemoveRequest.class));

		// when && then
		mockMvc.perform(
				delete("/api/v1/communities/{communityId}/emoji/{emojiId}", communityId, emojiId)
					.with(withJwt(1L, 1L, 1L)))
			.andDo(print())
			.andExpect(status().isNoContent());

		verify(emojiService).removeEmoji(any(EmojiRemoveRequest.class));
	}
}
