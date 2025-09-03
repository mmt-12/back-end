package com.memento.server.docs.post;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.post.PostController;
import com.memento.server.api.controller.post.dto.CreatePostRequest;
import com.memento.server.api.controller.post.dto.SearchAllPostResponse;
import com.memento.server.api.controller.post.dto.SearchPostResponse;
import com.memento.server.api.controller.post.dto.UpdatePostRequest;
import com.memento.server.api.controller.post.dto.read.Achievement;
import com.memento.server.api.controller.post.dto.read.CommentAuthor;
import com.memento.server.api.controller.post.dto.read.Emoji;
import com.memento.server.api.controller.post.dto.read.PostAuthor;
import com.memento.server.api.controller.post.dto.read.TemporaryVoice;
import com.memento.server.api.controller.post.dto.read.Voice;
import com.memento.server.api.service.post.PostService;
import com.memento.server.docs.RestDocsSupport;

public class PostControllerDocsTest extends RestDocsSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/memories/{memoryId}/posts";
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	private final PostService postService = mock(PostService.class);

	@Override
	protected Object initController() {
		return new PostController(postService);
	}

	@Test
	@DisplayName("포스트 상세 조회")
	public void searchTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		Long communityId = 1L;
		Long memoryId = 1L;
		Long postId = 1L;

		SearchPostResponse response = SearchPostResponse.builder()
			.id(1L)
			.author(PostAuthor.builder()
				.id(1L)
				.imageUrl("www.example.com")
				.nickname("example")
				.achievement(Achievement.builder()
					.id(1L)
					.name("example")
					.build())
				.build())
			.pictures(List.of("pic1.jpg"))
			.content("테스트 게시글입니다.")
			.createdAt(LocalDateTime.now())
			.comments(SearchPostResponse.Comment.builder()
				.emojis(List.of(Emoji.builder()
					.id(1L)
						.url("www.example.com")
						.name("test")
						.authors(List.of(CommentAuthor.builder()
								.id(1L)
								.commentId(1L)
								.imageUrl("www.example.com")
								.nickname("example")
								.achievement(Achievement.builder()
									.id(1L)
									.name("example")
									.build())
								.createdAt(LocalDateTime.now())
							.build()))
						.isInvolved(true)
					.build()))
				.voices(List.of(Voice.builder()
					.id(1L)
					.url("www.example.com")
					.name("test")
					.authors(List.of(CommentAuthor.builder()
						.id(1L)
						.commentId(1L)
						.imageUrl("www.example.com")
						.nickname("example")
						.achievement(Achievement.builder()
							.id(1L)
							.name("example")
							.build())
						.createdAt(LocalDateTime.now())
						.build()))
					.isInvolved(true)
					.build()))
				.temporaryVoices(List.of(TemporaryVoice.builder()
					.id(1L)
					.url("www.example.com")
					.name("test")
					.authors(List.of(CommentAuthor.builder()
						.id(1L)
						.commentId(1L)
						.imageUrl("www.example.com")
						.nickname("example")
						.achievement(Achievement.builder()
							.id(1L)
							.name("example")
							.build())
						.createdAt(LocalDateTime.now())
						.build()))
					.build()))
				.build())
			.build();

		when(postService.search(anyLong(), anyLong(), anyLong(), anyLong())).thenReturn(response);

		// when & then
		mockMvc.perform(
				get(PATH + "/{postId}", communityId, memoryId, postId))
			.andDo(print())
			.andExpect(status().isOk())
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
					fieldWithPath("comments.emojis[].name").type(STRING).description("이모지 이름"),
					fieldWithPath("comments.emojis[].involved").type(BOOLEAN).description("이모지 등록 여부"),
					fieldWithPath("comments.emojis[].authors").type(ARRAY).description("이모지 등록자 목록"),
					fieldWithPath("comments.emojis[].authors[].commentId").type(NUMBER).description("이모지 댓글 아이디"),
					fieldWithPath("comments.emojis[].authors[].id").type(NUMBER).description("이모지 등록자 아이디"),
					fieldWithPath("comments.emojis[].authors[].imageUrl").type(STRING).description("이모지 등록자 프로필 이미지"),
					fieldWithPath("comments.emojis[].authors[].nickname").type(STRING).description("이모지 등록자 이름"),
					fieldWithPath("comments.emojis[].authors[].achievement").type(OBJECT).description("이모지 등록자 업적"),
					fieldWithPath("comments.emojis[].authors[].achievement.id").type(NUMBER)
						.description("이모지 등록자 업적 아이디"),
					fieldWithPath("comments.emojis[].authors[].achievement.name").type(STRING)
						.description("이모지 등록자 업적 이름"),
					fieldWithPath("comments.emojis[].authors[].createdAt").type(STRING).description("이모지 등록자 등록 일시"),
					fieldWithPath("comments.voices").type(ARRAY).description("보이스 배열"),
					fieldWithPath("comments.voices[].id").type(NUMBER).description("보이스 아이디"),
					fieldWithPath("comments.voices[].url").type(STRING).description("보이스 경로"),
					fieldWithPath("comments.voices[].name").type(STRING).description("보이스 이름"),
					fieldWithPath("comments.voices[].involved").type(BOOLEAN).description("보이스 등록 여부"),
					fieldWithPath("comments.voices[].authors").type(ARRAY).description("보이스 등록자 목록"),
					fieldWithPath("comments.voices[].authors[].commentId").type(NUMBER).description("보이스 댓글 아이디"),
					fieldWithPath("comments.voices[].authors[].id").type(NUMBER).description("보이스 등록자 아이디"),
					fieldWithPath("comments.voices[].authors[].imageUrl").type(STRING).description("보이스 등록자 프로필 이미지"),
					fieldWithPath("comments.voices[].authors[].nickname").type(STRING).description("보이스 등록자 이름"),
					fieldWithPath("comments.voices[].authors[].achievement").type(OBJECT).description("보이스 등록자 업적"),
					fieldWithPath("comments.voices[].authors[].achievement.id").type(NUMBER)
						.description("보이스 등록자 업적 아이디"),
					fieldWithPath("comments.voices[].authors[].achievement.name").type(STRING)
						.description("보이스 등록자 업적 이름"),
					fieldWithPath("comments.voices[].authors[].createdAt").type(STRING).description("보이스 등록자 등록 일시"),
					fieldWithPath("comments.temporaryVoices").type(ARRAY).description("버블 보이스 배열"),
					fieldWithPath("comments.temporaryVoices[].id").type(NUMBER).description("버블 보이스 아이디"),
					fieldWithPath("comments.temporaryVoices[].url").type(STRING).description("버블 보이스 경로"),
					fieldWithPath("comments.temporaryVoices[].name").type(STRING).description("버블 보이스 이름"),
					fieldWithPath("comments.temporaryVoices[].authors").type(ARRAY).description("버블 등록자 목록"),
					fieldWithPath("comments.temporaryVoices[].authors[].commentId").type(NUMBER).description("버블 댓글 아이디"),
					fieldWithPath("comments.temporaryVoices[].authors[].id").type(NUMBER).description("버블 등록자 아이디"),
					fieldWithPath("comments.temporaryVoices[].authors[].imageUrl").type(STRING)
						.description("버블 등록자 프로필 이미지"),
					fieldWithPath("comments.temporaryVoices[].authors[].nickname").type(STRING)
						.description("버블 등록자 이름"),
					fieldWithPath("comments.temporaryVoices[].authors[].achievement").type(OBJECT)
						.description("버블 등록자 업적"),
					fieldWithPath("comments.temporaryVoices[].authors[].achievement.id").type(NUMBER)
						.description("버블 등록자 업적 아이디"),
					fieldWithPath("comments.temporaryVoices[].authors[].achievement.name").type(STRING)
						.description("버블 등록자 업적 이름"),
					fieldWithPath("comments.temporaryVoices[].authors[].createdAt").type(STRING)
						.description("버블 등록자 등록 일시")
				)
			));
	}

	@Test
	@DisplayName("포스트 목록 조회")
	public void searchAllTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		Long communityId = 1L;
		Long memoryId = 1L;
		int size = 10;
		Long cursor = 100L;

		SearchAllPostResponse response = SearchAllPostResponse.builder()
			.nextCursor(cursor)
			.hasNext(false)
			.posts(List.of(
				SearchPostResponse.builder()
					.id(1L)
					.author(PostAuthor.builder()
						.id(1L)
						.imageUrl("www.example.com")
						.nickname("example")
						.achievement(Achievement.builder()
							.id(1L)
							.name("example")
							.build())
						.build())
					.pictures(List.of("pic1.jpg"))
					.content("테스트 게시글입니다.")
					.createdAt(LocalDateTime.now())
					.comments(SearchPostResponse.Comment.builder()
						.emojis(List.of(Emoji.builder()
							.id(1L)
							.url("www.example.com")
							.name("test")
							.authors(List.of(CommentAuthor.builder()
								.id(1L)
								.commentId(1L)
								.imageUrl("www.example.com")
								.nickname("example")
								.achievement(Achievement.builder()
									.id(1L)
									.name("example")
									.build())
								.createdAt(LocalDateTime.now())
								.build()))
							.isInvolved(true)
							.build()))
						.voices(List.of(Voice.builder()
							.id(1L)
							.url("www.example.com")
							.name("test")
							.authors(List.of(CommentAuthor.builder()
								.id(1L)
								.commentId(1L)
								.imageUrl("www.example.com")
								.nickname("example")
								.achievement(Achievement.builder()
									.id(1L)
									.name("example")
									.build())
								.createdAt(LocalDateTime.now())
								.build()))
							.isInvolved(true)
							.build()))
						.temporaryVoices(List.of(TemporaryVoice.builder()
							.id(1L)
							.url("www.example.com")
							.name("test")
							.authors(List.of(CommentAuthor.builder()
								.id(1L)
								.commentId(1L)
								.imageUrl("www.example.com")
								.nickname("example")
								.achievement(Achievement.builder()
									.id(1L)
									.name("example")
									.build())
								.createdAt(LocalDateTime.now())
								.build()))
							.build()))
						.build())
					.build()
			))
			.build();


		when(postService.searchAll(anyLong(), anyLong(), anyLong(), anyInt(), any())).thenReturn(response);

		// when & then
		mockMvc.perform(
				get(PATH, communityId, memoryId))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("post-read-all-test",
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("nextCursor").type(NUMBER).description("다음 커서 값"),
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
					fieldWithPath("posts[].comments.emojis[].name").type(STRING).description("이모지 이름"),
					fieldWithPath("posts[].comments.emojis[].involved").type(BOOLEAN).description("이모지 등록 여부"),
					fieldWithPath("posts[].comments.emojis[].authors").type(ARRAY).description("이모지 등록자 목록"),
					fieldWithPath("posts[].comments.emojis[].authors[].commentId").type(NUMBER).description("이미지 댓글 아이디"),
					fieldWithPath("posts[].comments.emojis[].authors[].id").type(NUMBER).description("이모지 등록자 아이디"),
					fieldWithPath("posts[].comments.emojis[].authors[].imageUrl").type(STRING)
						.description("이모지 등록자 프로필 이미지"),
					fieldWithPath("posts[].comments.emojis[].authors[].nickname").type(STRING)
						.description("이모지 등록자 이름"),
					fieldWithPath("posts[].comments.emojis[].authors[].achievement").type(OBJECT)
						.description("이모지 등록자 업적"),
					fieldWithPath("posts[].comments.emojis[].authors[].achievement.id").type(NUMBER)
						.description("이모지 등록자 업적 아이디"),
					fieldWithPath("posts[].comments.emojis[].authors[].achievement.name").type(STRING)
						.description("이모지 등록자 업적 이름"),
					fieldWithPath("posts[].comments.emojis[].authors[].createdAt").type(STRING)
						.description("이모지 등록자 등록 일시"),
					fieldWithPath("posts[].comments.voices").type(ARRAY).description("보이스 배열"),
					fieldWithPath("posts[].comments.voices[].id").type(NUMBER).description("보이스 아이디"),
					fieldWithPath("posts[].comments.voices[].url").type(STRING).description("보이스 경로"),
					fieldWithPath("posts[].comments.voices[].name").type(STRING).description("보이스 이름"),
					fieldWithPath("posts[].comments.voices[].involved").type(BOOLEAN).description("보이스 등록 여부"),
					fieldWithPath("posts[].comments.voices[].authors").type(ARRAY).description("보이스 등록자 목록"),
					fieldWithPath("posts[].comments.voices[].authors[].commentId").type(NUMBER).description("보이스 댓글 아이디"),
					fieldWithPath("posts[].comments.voices[].authors[].id").type(NUMBER).description("보이스 등록자 아이디"),
					fieldWithPath("posts[].comments.voices[].authors[].imageUrl").type(STRING)
						.description("보이스 등록자 프로필 이미지"),
					fieldWithPath("posts[].comments.voices[].authors[].nickname").type(STRING)
						.description("보이스 등록자 이름"),
					fieldWithPath("posts[].comments.voices[].authors[].achievement").type(OBJECT)
						.description("보이스 등록자 업적"),
					fieldWithPath("posts[].comments.voices[].authors[].achievement.id").type(NUMBER)
						.description("보이스 등록자 업적 아이디"),
					fieldWithPath("posts[].comments.voices[].authors[].achievement.name").type(STRING)
						.description("보이스 등록자 업적 이름"),
					fieldWithPath("posts[].comments.voices[].authors[].createdAt").type(STRING)
						.description("보이스 등록자 등록 일시"),
					fieldWithPath("posts[].comments.temporaryVoices").type(ARRAY).description("버블 배열"),
					fieldWithPath("posts[].comments.temporaryVoices[].id").type(NUMBER).description("버블 아이디"),
					fieldWithPath("posts[].comments.temporaryVoices[].url").type(STRING).description("버블 경로"),
					fieldWithPath("posts[].comments.temporaryVoices[].name").type(STRING).description("버블 이름"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors").type(ARRAY).description("버블 등록자 목록"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].commentId").type(NUMBER).description("버블 댓글 아이디"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].id").type(NUMBER)
						.description("버블 등록자 아이디"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].imageUrl").type(STRING)
						.description("버블 등록자 프로필 이미지"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].nickname").type(STRING)
						.description("버블 등록자 이름"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].achievement").type(OBJECT)
						.description("버블 등록자 업적"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].achievement.id").type(NUMBER)
						.description("버블 등록자 업적 아이디"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].achievement.name").type(STRING)
						.description("버블 등록자 업적 이름"),
					fieldWithPath("posts[].comments.temporaryVoices[].authors[].createdAt").type(STRING)
						.description("버블 등록자 등록 일시")
				)
			));
	}

	@Test
	@DisplayName("포스트 생성")
	public void createTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		Long communityId = 1L;
		Long memoryId = 1L;

		CreatePostRequest request = CreatePostRequest.builder()
			.content("살려주세요")
			.build();
		String requestJson = objectMapper.writeValueAsString(request);

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

		doNothing().when(postService).create(anyLong(), anyLong(), anyLong(), anyString(), eq(List.of(file)));

		// when & then
		mockMvc.perform(
				multipart(PATH, communityId, memoryId)
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
		// given
		setAuthentication(1L, 1L, 1L);

		Long communityId = 1L;
		Long memoryId = 1L;
		Long postId = 1L;

		UpdatePostRequest request = UpdatePostRequest.builder()
			.content("수정된 내용")
			.oldPictures(new ArrayList<>(List.of(1L, 2L, 3L)))
			.build();
		String requestJson = objectMapper.writeValueAsString(request);

		MockMultipartFile requestPart = new MockMultipartFile(
			"request",
			"",
			"application/json",
			requestJson.getBytes()
		);

		MockMultipartFile file = new MockMultipartFile(
			"nswPictures",
			"newImage1.png",
			"image/png",
			"new image content 1".getBytes()
		);

		doNothing().when(postService).update(anyLong(), anyLong(), anyLong(), anyLong(), anyString(), anyList(), eq(List.of(file)));

		// when & then
		mockMvc.perform(
				multipart(PATH + "/{postId}", communityId, memoryId, postId)
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
	public void deleteTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		Long communityId = 1L;
		Long memoryId = 1L;
		Long postId = 1L;

		doNothing().when(postService).delete(anyLong(), anyLong(), anyLong(), anyLong());

		// when & then
		mockMvc.perform(
				delete(PATH + "/{postId}", communityId, memoryId, postId))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("post-delete-test"));
	}
}
