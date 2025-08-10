package com.memento.server.spring.api.controller.comment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.comment.dto.EmojiCommentCreateRequest;
import com.memento.server.api.controller.comment.dto.VoiceCommentCreateRequest;
import com.memento.server.spring.api.controller.ControllerTestSupport;

public class CommentControllerTest extends ControllerTestSupport {

	@Test
	@DisplayName("이모지 댓글을 생성한다.")
	void createEmojiComment() throws Exception {
		// given
		long postId = 1L;
		long communityId = 1L;
		long memoryId = 1L;
		EmojiCommentCreateRequest request = EmojiCommentCreateRequest.builder()
			.emojiId(1L)
			.build();

		doNothing().when(commentService).createEmojiComment(any());

		// when & then
		mockMvc.perform(
				post("/api/v1/communities/{communityId}/memories/{memoryId}/posts/{postId}/comments/emoji", 
					communityId, memoryId, postId)
					.with(withJwt(1L, 1L, 1L))
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated());

		verify(commentService).createEmojiComment(any());
	}

	@Test
	@DisplayName("이모지 댓글 생성 시 emojiId는 필수값이다.")
	void createEmojiCommentWithoutEmojiId() throws Exception {
		// given
		long postId = 1L;
		long communityId = 1L;
		long memoryId = 1L;
		EmojiCommentCreateRequest request = EmojiCommentCreateRequest.builder().build();

		doNothing().when(commentService).createEmojiComment(any());

		// when & then
		mockMvc.perform(
				post("/api/v1/communities/{communityId}/memories/{memoryId}/posts/{postId}/comments/emoji",
					communityId, memoryId, postId)
					.with(withJwt(1L, 1L, 1L))
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("emojiId"))
			.andExpect(jsonPath("$.errors[0].message").value("emojiId 값은 필수입니다."));

		verify(commentService, never()).createEmojiComment(any());
	}

	@Test
	@DisplayName("보이스 댓글을 생성한다.")
	void createVoiceComment() throws Exception {
		// given
		long postId = 1L;
		long communityId = 1L;
		long memoryId = 1L;
		VoiceCommentCreateRequest request = VoiceCommentCreateRequest.builder()
			.voiceId(1L)
			.build();

		doNothing().when(commentService).createVoiceComment(any());

		// when & then
		mockMvc.perform(
				post("/api/v1/communities/{communityId}/memories/{memoryId}/posts/{postId}/comments/voices", 
					communityId, memoryId, postId)
					.with(withJwt(1L, 1L, 1L))
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated());

		verify(commentService).createVoiceComment(any());
	}

	@Test
	@DisplayName("보이스 댓글 생성 시 voiceId는 필수값이다.")
	void createVoiceCommentWithoutVoiceId() throws Exception {
		// given
		long postId = 1L;
		long communityId = 1L;
		long memoryId = 1L;
		VoiceCommentCreateRequest request = VoiceCommentCreateRequest.builder().build();

		doNothing().when(commentService).createVoiceComment(any());

		// when & then
		mockMvc.perform(
				post("/api/v1/communities/{communityId}/memories/{memoryId}/posts/{postId}/comments/voices",
					communityId, memoryId, postId)
					.with(withJwt(1L, 1L, 1L))
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("voiceId"))
			.andExpect(jsonPath("$.errors[0].message").value("voiceId 값은 필수입니다."));

		verify(commentService, never()).createVoiceComment(any());
	}

	@Test
	@DisplayName("임시 보이스 댓글을 생성한다.")
	void createTemporaryVoiceComment() throws Exception {
		// given
		long postId = 1L;
		long communityId = 1L;
		long memoryId = 1L;

		MockMultipartFile voice = new MockMultipartFile(
			"voice",
			"voice.wav",
			"audio/wav",
			"voice content".getBytes()
		);

		doNothing().when(commentService).createTemporaryVoiceComment(any());

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/memories/{memoryId}/posts/{postId}/comments/bubble", 
					communityId, memoryId, postId)
					.file(voice)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated());

		verify(commentService).createTemporaryVoiceComment(any());
	}

	@Test
	@DisplayName("임시 보이스 댓글 생성 시 voice는 필수값이다.")
	void createTemporaryVoiceCommentWithoutVoice() throws Exception {
		// given
		long postId = 1L;
		long communityId = 1L;
		long memoryId = 1L;

		doNothing().when(commentService).createTemporaryVoiceComment(any());

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/memories/{memoryId}/posts/{postId}/comments/bubble", 
					communityId, memoryId, postId)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1002))
			.andExpect(jsonPath("$.message").value("필수 요청 part 누락"))
			.andExpect(jsonPath("$.errors[0].field").value("voice"))
			.andExpect(jsonPath("$.errors[0].message").value("voice은(는) 필수입니다."));

		verify(commentService, never()).createTemporaryVoiceComment(any());
	}

	@Test
	@DisplayName("댓글을 삭제한다.")
	void deleteComment() throws Exception {
		// given
		long postId = 1L;
		long communityId = 1L;
		long memoryId = 1L;
		long commentId = 1L;

		doNothing().when(commentService).deleteComment(any());

		// when & then
		mockMvc.perform(
				delete("/api/v1/communities/{communityId}/memories/{memoryId}/posts/{postId}/comments/{commentId}", 
					communityId, memoryId, postId, commentId)
					.with(withJwt(1L, 1L, 1L)))
			.andDo(print())
			.andExpect(status().isNoContent());

		verify(commentService).deleteComment(any());
	}
}