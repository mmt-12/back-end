package com.memento.server.docs.post;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.post.PostController;
import com.memento.server.api.controller.post.dto.CreatePostRequest;
import com.memento.server.api.controller.post.dto.ReadAllPostResponse;
import com.memento.server.api.controller.post.dto.ReadPostResponse;
import com.memento.server.api.controller.post.dto.UpdatePostRequest;
import com.memento.server.docs.RestDocsSupport;

public class PostControllerTestDocsTest extends RestDocsSupport {

	public static final String PATH = "/api/v1/groups/{groupId}/memories/{memoryId}/posts";
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	@Override
	protected Object initController() {
		return new PostController();
	}

	@Test
	@DisplayName("포스트 상세 조회")
	public void readTest() throws Exception {
		// given
		Long groupId = 1L;
		Long memoryId = 1L;
		Long postId = 1L;

		// when & then
		ReadPostResponse response = ReadPostResponse.from();

		mockMvc.perform(
				get(PATH + "/{postId}", groupId, memoryId, postId))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(response.id()))
			.andExpect(jsonPath("$.author.id").value(response.author().getId()))
			.andExpect(jsonPath("$.author.nickname").value(response.author().getNickname()))
			.andExpect(jsonPath("$.author.imageUrl").value(response.author().getImageUrl()))
			.andExpect(jsonPath("$.author.achievement.id").value(response.author().getAchievement().id()))
			.andExpect(jsonPath("$.author.achievement.name").value(response.author().getAchievement().name()))
			.andExpect(jsonPath("$.pictures[0]").value(response.pictures().get(0)))
			.andExpect(jsonPath("$.content").value(response.content()))
			.andExpect(jsonPath("$.createdAt").value(response.createdAt().format(formatter)))

			// comments.emojis[0]
			.andExpect(jsonPath("$.comments.emojis[0].id").value(response.comments().getEmojis().get(0).getId()))
			.andExpect(jsonPath("$.comments.emojis[0].url").value(response.comments().getEmojis().get(0).getUrl()))
			.andExpect(jsonPath("$.comments.emojis[0].involved").value(response.comments().getEmojis().get(0).isInvolved()))

			// comments.emojis[0].authors[0]
			.andExpect(jsonPath("$.comments.emojis[0].authors[0].id").value(response.comments().getEmojis().get(0).getAuthors().get(0).getId()))
			.andExpect(jsonPath("$.comments.emojis[0].authors[0].imageUrl").value(response.comments().getEmojis().get(0).getAuthors().get(0).getImageUrl()))
			.andExpect(jsonPath("$.comments.emojis[0].authors[0].nickname").value(response.comments().getEmojis().get(0).getAuthors().get(0).getNickname()))
			.andExpect(jsonPath("$.comments.emojis[0].authors[0].achievement.id").value(response.comments().getEmojis().get(0).getAuthors().get(0).getAchievement().id()))
			.andExpect(jsonPath("$.comments.emojis[0].authors[0].achievement.name").value(response.comments().getEmojis().get(0).getAuthors().get(0).getAchievement().name()))
			.andExpect(jsonPath("$.comments.emojis[0].authors[0].createdAt").value(
				response.comments().getEmojis().get(0).getAuthors().get(0).getCreatedAt().format(formatter)
			))

			// comments.voices[0]
			.andExpect(jsonPath("$.comments.voices[0].id").value(response.comments().getVoices().get(0).getId()))
			.andExpect(jsonPath("$.comments.voices[0].url").value(response.comments().getVoices().get(0).getUrl()))
			.andExpect(jsonPath("$.comments.voices[0].involved").value(response.comments().getVoices().get(0).isInvolved()))

			// comments.voices[0].authors[0]
			.andExpect(jsonPath("$.comments.voices[0].authors[0].id").value(response.comments().getVoices().get(0).getAuthors().get(0).getId()))
			.andExpect(jsonPath("$.comments.voices[0].authors[0].imageUrl").value(response.comments().getVoices().get(0).getAuthors().get(0).getImageUrl()))
			.andExpect(jsonPath("$.comments.voices[0].authors[0].nickname").value(response.comments().getVoices().get(0).getAuthors().get(0).getNickname()))
			.andExpect(jsonPath("$.comments.voices[0].authors[0].achievement.id").value(response.comments().getVoices().get(0).getAuthors().get(0).getAchievement().id()))
			.andExpect(jsonPath("$.comments.voices[0].authors[0].achievement.name").value(response.comments().getVoices().get(0).getAuthors().get(0).getAchievement().name()))
			.andExpect(jsonPath("$.comments.voices[0].authors[0].createdAt").value(
				response.comments().getVoices().get(0).getAuthors().get(0).getCreatedAt().format(formatter)
			))

			// comments.temporaryVoices[0]
			.andExpect(jsonPath("$.comments.temporaryVoices[0].id").value(response.comments().getTemporaryVoices().get(0).getId()))
			.andExpect(jsonPath("$.comments.temporaryVoices[0].url").value(response.comments().getTemporaryVoices().get(0).getUrl()))

			// comments.temporaryVoices[0].authors[0]
			.andExpect(jsonPath("$.comments.temporaryVoices[0].authors[0].id").value(response.comments().getTemporaryVoices().get(0).getAuthors().get(0).getId()))
			.andExpect(jsonPath("$.comments.temporaryVoices[0].authors[0].imageUrl").value(response.comments().getTemporaryVoices().get(0).getAuthors().get(0).getImageUrl()))
			.andExpect(jsonPath("$.comments.temporaryVoices[0].authors[0].nickname").value(response.comments().getTemporaryVoices().get(0).getAuthors().get(0).getNickname()))
			.andExpect(jsonPath("$.comments.temporaryVoices[0].authors[0].achievement.id").value(response.comments().getTemporaryVoices().get(0).getAuthors().get(0).getAchievement().id()))
			.andExpect(jsonPath("$.comments.temporaryVoices[0].authors[0].achievement.name").value(response.comments().getTemporaryVoices().get(0).getAuthors().get(0).getAchievement().name()))
			.andExpect(jsonPath("$.comments.temporaryVoices[0].authors[0].createdAt").value(
					response.comments().getTemporaryVoices().get(0).getAuthors().get(0).getCreatedAt().format(formatter)
				))
			.andDo(document("post-read-test",
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("id").type(NUMBER).description("포스트 아이디"),
					fieldWithPath("pictures").type(ARRAY).description("포스트 이미지 URL 배열"),
					fieldWithPath("pictures[].[]").optional().type(STRING).description("포스트 이미지 URL"),
					fieldWithPath("content").type(STRING).description("포스트 내용"),
					fieldWithPath("author").type(OBJECT).description("포스트 작성자"),
					fieldWithPath("author.id").type(NUMBER).description("작성자 아이디"),
					fieldWithPath("author.nickname").type(STRING).description("작성자 이름"),
					fieldWithPath("author.imageUrl").type(STRING).description("작성자 프로필 이미지"),
					fieldWithPath("author.achievement").type(OBJECT).description("작성자 업적"),
					fieldWithPath("author.achievement.id").type(NUMBER).description("작성자 업적 아이디"),
					fieldWithPath("author.achievement.name").type(STRING).description("작성자 업적 이름"),
					fieldWithPath("createdAt").type(STRING).description("포스트 작성 일시"),
					fieldWithPath("comments").type(OBJECT).description("포스트에 달린 댓글"),
					fieldWithPath("comments.emojis").type(ARRAY).description("이모지 배열"),
					fieldWithPath("comments.emojis[].id").type(NUMBER).description("이모지 아이디"),
					fieldWithPath("comments.emojis[].url").type(STRING).description("이모지 경로"),
					fieldWithPath("comments.emojis[].involved").type(BOOLEAN).description("이모지 등록 여부"),
					fieldWithPath("comments.emojis[].authors").type(ARRAY).description("이모지 등록자 목록"),
					fieldWithPath("comments.emojis[].authors[].id").type(NUMBER).description("이모지 등록자 아이디"),
					fieldWithPath("comments.emojis[].authors[].imageUrl").type(STRING).description("이모지 등록자 프로필 이미지"),
					fieldWithPath("comments.emojis[].authors[].nickname").type(STRING).description("이모지 등록자 이름"),
					fieldWithPath("comments.emojis[].authors[].achievement").type(OBJECT).description("이모지 등록자 업적"),
					fieldWithPath("comments.emojis[].authors[].achievement.id").type(NUMBER).description("이모지 등록자 업적 아이디"),
					fieldWithPath("comments.emojis[].authors[].achievement.name").type(STRING).description("이모지 등록자 업적 이름"),
					fieldWithPath("comments.emojis[].authors[].createdAt").type(STRING).description("이모지 등록자 등록 일시"),
					fieldWithPath("comments.voices").type(ARRAY).description("포스트 아이디"),
					fieldWithPath("comments.voices[].id").type(NUMBER).description("포스트 아이디"),
					fieldWithPath("comments.voices[].url").type(STRING).description("포스트 아이디"),
					fieldWithPath("comments.voices[].involved").type(BOOLEAN).description("포스트 아이디"),
					fieldWithPath("comments.voices[].authors").type(ARRAY).description("보이스 등록자 목록"),
					fieldWithPath("comments.voices[].authors[].id").type(NUMBER).description("보이스 등록자 아이디"),
					fieldWithPath("comments.voices[].authors[].imageUrl").type(STRING).description("보이스 등록자 프로필 이미지"),
					fieldWithPath("comments.voices[].authors[].nickname").type(STRING).description("보이스 등록자 이름"),
					fieldWithPath("comments.voices[].authors[].achievement").type(OBJECT).description("보이스 등록자 업적"),
					fieldWithPath("comments.voices[].authors[].achievement.id").type(NUMBER).description("보이스 등록자 업적 아이디"),
					fieldWithPath("comments.voices[].authors[].achievement.name").type(STRING).description("보이스 등록자 업적 이름"),
					fieldWithPath("comments.voices[].authors[].createdAt").type(STRING).description("보이스 등록자 등록 일시"),
					fieldWithPath("comments.temporaryVoices").type(ARRAY).description("포스트 아이디"),
					fieldWithPath("comments.temporaryVoices[].id").type(NUMBER).description("포스트 아이디"),
					fieldWithPath("comments.temporaryVoices[].url").type(STRING).description("포스트 아이디"),
					fieldWithPath("comments.temporaryVoices[].authors").type(ARRAY).description("버블 등록자 목록"),
					fieldWithPath("comments.temporaryVoices[].authors[].id").type(NUMBER).description("버블 등록자 아이디"),
					fieldWithPath("comments.temporaryVoices[].authors[].imageUrl").type(STRING).description("버블 등록자 프로필 이미지"),
					fieldWithPath("comments.temporaryVoices[].authors[].nickname").type(STRING).description("버블 등록자 이름"),
					fieldWithPath("comments.temporaryVoices[].authors[].achievement").type(OBJECT).description("버블 등록자 업적"),
					fieldWithPath("comments.temporaryVoices[].authors[].achievement.id").type(NUMBER).description("버블 등록자 업적 아이디"),
					fieldWithPath("comments.temporaryVoices[].authors[].achievement.name").type(STRING).description("버블 등록자 업적 이름"),
					fieldWithPath("comments.temporaryVoices[].authors[].createdAt").type(STRING).description("버블 등록자 등록 일시")
				)
			));
	}

	@Test
	@DisplayName("포스트 목록 조회")
	public void readAllTest() throws Exception{
		// given
		Long groupId = 1L;
		Long memoryId = 1L;

		// when & then
		ReadAllPostResponse response = ReadAllPostResponse.from();

		mockMvc.perform(
				get(PATH, groupId, memoryId))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.cursor").value(response.cursor()))
			.andExpect(jsonPath("$.hasNext").value(response.hasNext()))
			.andExpect(jsonPath("$.posts[0].id").value(response.posts().get(0).id()))
			.andExpect(jsonPath("$.posts[0].author.id").value(response.posts().get(0).author().getId()))
			.andExpect(jsonPath("$.posts[0].author.nickname").value(response.posts().get(0).author().getNickname()))
			.andExpect(jsonPath("$.posts[0].author.imageUrl").value(response.posts().get(0).author().getImageUrl()))
			.andExpect(jsonPath("$.posts[0].author.achievement.id").value(response.posts().get(0).author().getAchievement().id()))
			.andExpect(jsonPath("$.posts[0].author.achievement.name").value(response.posts().get(0).author().getAchievement().name()))
			.andExpect(jsonPath("$.posts[0].pictures[0]").value(response.posts().get(0).pictures().get(0)))
			.andExpect(jsonPath("$.posts[0].content").value(response.posts().get(0).content()))
			.andExpect(jsonPath("$.posts[0].createdAt").value(response.posts().get(0).createdAt().format(formatter)))

			// comments.emojis[0]
			.andExpect(jsonPath("$.posts[0].comments.emojis[0].id").value(response.posts().get(0).comments().getEmojis().get(0).getId()))
			.andExpect(jsonPath("$.posts[0].comments.emojis[0].url").value(response.posts().get(0).comments().getEmojis().get(0).getUrl()))
			.andExpect(jsonPath("$.posts[0].comments.emojis[0].involved").value(response.posts().get(0).comments().getEmojis().get(0).isInvolved()))

			// comments.emojis[0].authors[0]
			.andExpect(jsonPath("$.posts[0].comments.emojis[0].authors[0].id").value(response.posts().get(0).comments().getEmojis().get(0).getAuthors().get(0).getId()))
			.andExpect(jsonPath("$.posts[0].comments.emojis[0].authors[0].imageUrl").value(response.posts().get(0).comments().getEmojis().get(0).getAuthors().get(0).getImageUrl()))
			.andExpect(jsonPath("$.posts[0].comments.emojis[0].authors[0].nickname").value(response.posts().get(0).comments().getEmojis().get(0).getAuthors().get(0).getNickname()))
			.andExpect(jsonPath("$.posts[0].comments.emojis[0].authors[0].achievement.id").value(response.posts().get(0).comments().getEmojis().get(0).getAuthors().get(0).getAchievement().id()))
			.andExpect(jsonPath("$.posts[0].comments.emojis[0].authors[0].achievement.name").value(response.posts().get(0).comments().getEmojis().get(0).getAuthors().get(0).getAchievement().name()))
			.andExpect(jsonPath("$.posts[0].comments.emojis[0].authors[0].createdAt").value(
				response.posts().get(0).comments().getEmojis().get(0).getAuthors().get(0).getCreatedAt().format(formatter)
			))

			// comments.voices[0]
			.andExpect(jsonPath("$.posts[0].comments.voices[0].id").value(response.posts().get(0).comments().getVoices().get(0).getId()))
			.andExpect(jsonPath("$.posts[0].comments.voices[0].url").value(response.posts().get(0).comments().getVoices().get(0).getUrl()))
			.andExpect(jsonPath("$.posts[0].comments.voices[0].involved").value(response.posts().get(0).comments().getVoices().get(0).isInvolved()))

			// comments.voices[0].authors[0]
			.andExpect(jsonPath("$.posts[0].comments.voices[0].authors[0].id").value(response.posts().get(0).comments().getVoices().get(0).getAuthors().get(0).getId()))
			.andExpect(jsonPath("$.posts[0].comments.voices[0].authors[0].imageUrl").value(response.posts().get(0).comments().getVoices().get(0).getAuthors().get(0).getImageUrl()))
			.andExpect(jsonPath("$.posts[0].comments.voices[0].authors[0].nickname").value(response.posts().get(0).comments().getVoices().get(0).getAuthors().get(0).getNickname()))
			.andExpect(jsonPath("$.posts[0].comments.voices[0].authors[0].achievement.id").value(response.posts().get(0).comments().getVoices().get(0).getAuthors().get(0).getAchievement().id()))
			.andExpect(jsonPath("$.posts[0].comments.voices[0].authors[0].achievement.name").value(response.posts().get(0).comments().getVoices().get(0).getAuthors().get(0).getAchievement().name()))
			.andExpect(jsonPath("$.posts[0].comments.voices[0].authors[0].createdAt").value(
				response.posts().get(0).comments().getVoices().get(0).getAuthors().get(0).getCreatedAt().format(formatter)
			))

			// comments.temporaryVoices[0]
			.andExpect(jsonPath("$.posts[0].comments.temporaryVoices[0].id").value(response.posts().get(0).comments().getTemporaryVoices().get(0).getId()))
			.andExpect(jsonPath("$.posts[0].comments.temporaryVoices[0].url").value(response.posts().get(0).comments().getTemporaryVoices().get(0).getUrl()))

			// comments.temporaryVoices[0].authors[0]
			.andExpect(jsonPath("$.posts[0].comments.temporaryVoices[0].authors[0].id").value(response.posts().get(0).comments().getTemporaryVoices().get(0).getAuthors().get(0).getId()))
			.andExpect(jsonPath("$.posts[0].comments.temporaryVoices[0].authors[0].imageUrl").value(response.posts().get(0).comments().getTemporaryVoices().get(0).getAuthors().get(0).getImageUrl()))
			.andExpect(jsonPath("$.posts[0].comments.temporaryVoices[0].authors[0].nickname").value(response.posts().get(0).comments().getTemporaryVoices().get(0).getAuthors().get(0).getNickname()))
			.andExpect(jsonPath("$.posts[0].comments.temporaryVoices[0].authors[0].achievement.id").value(response.posts().get(0).comments().getTemporaryVoices().get(0).getAuthors().get(0).getAchievement().id()))
			.andExpect(jsonPath("$.posts[0].comments.temporaryVoices[0].authors[0].achievement.name").value(response.posts().get(0).comments().getTemporaryVoices().get(0).getAuthors().get(0).getAchievement().name()))
			.andExpect(jsonPath("$.posts[0].comments.temporaryVoices[0].authors[0].createdAt").value(
				response.posts().get(0).comments().getTemporaryVoices().get(0).getAuthors().get(0).getCreatedAt().format(formatter)
			))
			.andDo(document("post-read-all-test",
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("cursor").type(NUMBER).description("커서 값"),
					fieldWithPath("hasNext").type(BOOLEAN).description("다음 값 존재 여부"),
					fieldWithPath("posts").type(ARRAY).description("포스트 목록"),
					fieldWithPath("posts[].id").type(NUMBER).description("포스트 아이디"),
					fieldWithPath("posts[].pictures").type(ARRAY).description("포스트 이미지 URL 배열"),
					fieldWithPath("posts[].pictures[].[]").optional().type(STRING).description("포스트 이미지 URL"),
					fieldWithPath("posts[].content").type(STRING).description("포스트 내용"),
					fieldWithPath("posts[].author").type(OBJECT).description("포스트 작성자"),
					fieldWithPath("posts[].author.id").type(NUMBER).description("작성자 아이디"),
					fieldWithPath("posts[].author.nickname").type(STRING).description("작성자 이름"),
					fieldWithPath("posts[].author.imageUrl").type(STRING).description("작성자 프로필 이미지"),
					fieldWithPath("posts[].author.achievement").type(OBJECT).description("작성자 업적"),
					fieldWithPath("posts[].author.achievement.id").type(NUMBER).description("작성자 업적 아이디"),
					fieldWithPath("posts[].author.achievement.name").type(STRING).description("작성자 업적 이름"),
					fieldWithPath("posts[].createdAt").type(STRING).description("포스트 작성 일시"),
					fieldWithPath("posts[].comments").type(OBJECT).description("포스트에 달린 댓글"),
					fieldWithPath("posts[].comments.emojis").type(ARRAY).description("이모지 배열"),
					fieldWithPath("posts[].comments.emojis[].id").type(NUMBER).description("이모지 아이디"),
					fieldWithPath("posts[].comments.emojis[].url").type(STRING).description("이모지 경로"),
					fieldWithPath("posts[].comments.emojis[].involved").type(BOOLEAN).description("이모지 등록 여부"),
					fieldWithPath("posts[].comments.emojis[].authors").type(ARRAY).description("이모지 등록자 목록"),
					fieldWithPath("posts[].comments.emojis[].authors[].id").type(NUMBER).description("이모지 등록자 아이디"),
					fieldWithPath("posts[].comments.emojis[].authors[].imageUrl").type(STRING).description("이모지 등록자 프로필 이미지"),
					fieldWithPath("posts[].comments.emojis[].authors[].nickname").type(STRING).description("이모지 등록자 이름"),
					fieldWithPath("posts[].comments.emojis[].authors[].achievement").type(OBJECT).description("이모지 등록자 업적"),
					fieldWithPath("posts[].comments.emojis[].authors[].achievement.id").type(NUMBER).description("이모지 등록자 업적 아이디"),
					fieldWithPath("posts[].comments.emojis[].authors[].achievement.name").type(STRING).description("이모지 등록자 업적 이름"),
					fieldWithPath("posts[].comments.emojis[].authors[].createdAt").type(STRING).description("이모지 등록자 등록 일시"),
					fieldWithPath("posts[].comments.voices").type(ARRAY).description("포스트 아이디"),
					fieldWithPath("posts[].comments.voices[].id").type(NUMBER).description("포스트 아이디"),
					fieldWithPath("posts[].comments.voices[].url").type(STRING).description("포스트 아이디"),
					fieldWithPath("posts[].comments.voices[].involved").type(BOOLEAN).description("포스트 아이디"),
					fieldWithPath("posts[].comments.voices[].authors").type(ARRAY).description("보이스 등록자 목록"),
					fieldWithPath("posts[].comments.voices[].authors[].id").type(NUMBER).description("보이스 등록자 아이디"),
					fieldWithPath("posts[].comments.voices[].authors[].imageUrl").type(STRING).description("보이스 등록자 프로필 이미지"),
					fieldWithPath("posts[].comments.voices[].authors[].nickname").type(STRING).description("보이스 등록자 이름"),
					fieldWithPath("posts[].comments.voices[].authors[].achievement").type(OBJECT).description("보이스 등록자 업적"),
					fieldWithPath("posts[].comments.voices[].authors[].achievement.id").type(NUMBER).description("보이스 등록자 업적 아이디"),
					fieldWithPath("posts[].comments.voices[].authors[].achievement.name").type(STRING).description("보이스 등록자 업적 이름"),
					fieldWithPath("posts[].comments.voices[].authors[].createdAt").type(STRING).description("보이스 등록자 등록 일시"),
					fieldWithPath("posts[].comments.temporaryVoices").type(ARRAY).description("포스트 아이디"),
					fieldWithPath("posts[].comments.temporaryVoices[].id").type(NUMBER).description("포스트 아이디"),
					fieldWithPath("posts[].comments.temporaryVoices[].url").type(STRING).description("포스트 아이디"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors").type(ARRAY).description("버블 등록자 목록"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].id").type(NUMBER).description("버블 등록자 아이디"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].imageUrl").type(STRING).description("버블 등록자 프로필 이미지"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].nickname").type(STRING).description("버블 등록자 이름"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].achievement").type(OBJECT).description("버블 등록자 업적"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].achievement.id").type(NUMBER).description("버블 등록자 업적 아이디"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].achievement.name").type(STRING).description("버블 등록자 업적 이름"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].createdAt").type(STRING).description("버블 등록자 등록 일시")
				)
			));
	}

	@Test
	@DisplayName("포스트 생성")
	public void createTest() throws Exception{
		// given
		Long groupId = 1L;
		Long memoryId = 1L;

		CreatePostRequest request = CreatePostRequest.builder()
			.content("살려주세요")
			.build();
		String requestJson = objectMapper.writeValueAsString(request);

		// JSON 파트
		MockMultipartFile requestPart = new MockMultipartFile(
			"request",
			"",
			"application/json",
			requestJson.getBytes()
		);

		MockMultipartFile file = new MockMultipartFile(
			"pictures",
			"image.png",
			"image/png",
			"sample image content".getBytes()
		);

		// when & then
		mockMvc.perform(
				multipart(PATH, groupId, memoryId)
					.file(requestPart)
					.file(file)
					.contentType(MULTIPART_FORM_DATA)
					.accept(APPLICATION_JSON)
					.with(req -> {
						req.setMethod("POST"); // 명시적 POST
						return req;
					})
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("post-create-test",
				preprocessRequest(prettyPrint()),
				requestParts(
					partWithName("request").description("게시글 생성 요청"),
					partWithName("pictures").description("이미지 목록")
				)
			));
	}

	@Test
	@DisplayName("포스트 수정")
	public void updateTest() throws Exception {
		Long groupId = 1L;
		Long memoryId = 1L;
		Long postId = 1L;

		UpdatePostRequest request = UpdatePostRequest.builder()
			.content("수정된 내용")
			.oldPictures(new ArrayList<>(List.of(1L, 2L, 3L)))
			.build();
		String requestJson = objectMapper.writeValueAsString(request);

		// JSON 파트
		MockMultipartFile requestPart = new MockMultipartFile(
			"request",
			"",
			"application/json",
			requestJson.getBytes()
		);

		// 파일 여러 개
		MockMultipartFile file = new MockMultipartFile(
			"nswPictures",
			"newImage1.png",
			"image/png",
			"new image content 1".getBytes()
		);


		// when & then
		mockMvc.perform(
				multipart(PATH + "/{postId}", groupId, memoryId, postId)
					.file(requestPart)
					.file(file)
					.contentType(MULTIPART_FORM_DATA)
					.accept(APPLICATION_JSON)
					.with(req -> {
						req.setMethod("PUT");
						return req;
					})
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("post-update-test",
				preprocessRequest(prettyPrint()),
				requestParts(
					partWithName("request").description("게시글 수정 요청"),
					partWithName("nswPictures").optional()
						.description("추가할 이미지 파일 리스트")
				)
			));
	}

	@Test
	@DisplayName("포스트 삭제")
	public void deleteTest() throws Exception{
		// given
		Long groupId = 1L;
		Long memoryId = 1L;
		Long postId = 1L;

		// when & then
		mockMvc.perform(
				delete(PATH + "/{postId}", groupId, memoryId, postId))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("post-delete-test"));
	}
}
