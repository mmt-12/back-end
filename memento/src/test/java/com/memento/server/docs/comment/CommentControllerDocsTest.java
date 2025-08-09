package com.memento.server.docs.comment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.comment.CommentController;
import com.memento.server.api.controller.comment.dto.EmojiCommentCreateRequest;
import com.memento.server.api.controller.comment.dto.VoiceCommentCreateRequest;
import com.memento.server.api.service.comment.CommentService;
import com.memento.server.docs.RestDocsSupport;

public class CommentControllerDocsTest extends RestDocsSupport {

	private final CommentService commentService = Mockito.mock(CommentService.class);

	@Override
	protected Object initController() {
		return new CommentController(commentService);
	}

	@Test
	@DisplayName("이모지 댓글을 생성한다.")
	void createEmojiComment() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

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
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("comment-emoji-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("커뮤니티 ID"),
					parameterWithName("memoryId").description("메모리 ID"),
					parameterWithName("postId").description("포스트 ID")
				),
				requestFields(
					fieldWithPath("emojiId").type(NUMBER).description("이모지 ID")
				)
			));

		verify(commentService).createEmojiComment(any());
	}

	@Test
	@DisplayName("보이스 댓글을 생성한다.")
	void createVoiceComment() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

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
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("comment-voice-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("커뮤니티 ID"),
					parameterWithName("memoryId").description("메모리 ID"),
					parameterWithName("postId").description("포스트 ID")
				),
				requestFields(
					fieldWithPath("voiceId").type(NUMBER).description("보이스 ID")
				)
			));

		verify(commentService).createVoiceComment(any());
	}

	@Test
	@DisplayName("임시 보이스 댓글을 생성한다.")
	void createTemporaryVoiceComment() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

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
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("comment-temporary-voice-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("커뮤니티 ID"),
					parameterWithName("memoryId").description("메모리 ID"),
					parameterWithName("postId").description("포스트 ID")
				),
				requestParts(
					partWithName("voice").description("임시 보이스 음성 파일 (WAV 형식)")
				)
			));

		verify(commentService).createTemporaryVoiceComment(any());
	}

	@Test
	@DisplayName("댓글을 삭제한다.")
	void deleteComment() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		long postId = 1L;
		long communityId = 1L;
		long memoryId = 1L;
		long commentId = 1L;

		doNothing().when(commentService).deleteComment(any());

		// when & then
		mockMvc.perform(
				delete("/api/v1/communities/{communityId}/memories/{memoryId}/posts/{postId}/comments/{commentId}", 
					communityId, memoryId, postId, commentId))
			.andDo(print())
			.andExpect(status().isNoContent())
			.andDo(document("comment-delete",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("커뮤니티 ID"),
					parameterWithName("memoryId").description("메모리 ID"),
					parameterWithName("postId").description("포스트 ID"),
					parameterWithName("commentId").description("삭제할 댓글 ID")
				)
			));

		verify(commentService).deleteComment(any());
	}
}