package com.memento.server.spring.api.controller.post;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.post.dto.request.CreatePostRequest;
import com.memento.server.api.controller.post.dto.request.UpdatePostRequest;
import com.memento.server.spring.api.controller.ControllerTestSupport;

public class PostControllerTest extends ControllerTestSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/memories/{memoryId}/posts";

	@Test
	@DisplayName("포스트 상세 조회")
	void searchTest() throws Exception {
		// given
		Long communityId = 1L;
		Long memoryId = 1L;
		Long postId = 1L;

		when(postService.search(anyLong(), anyLong(), anyLong(), anyLong())).thenReturn(null);

		// when & then
		mockMvc.perform(
				get(PATH + "/{postId}", communityId, memoryId, postId)
					.with(withJwt(1L, 2L, 1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("다른 커뮤니티는 조회할 수 없다")
	void searchWithDifferentCommunityTest() throws Exception {
		// given
		Long communityId = 1L;
		Long memoryId = 1L;
		Long postId = 1L;

		when(postService.search(anyLong(), anyLong(), anyLong(), anyLong())).thenReturn(null);

		// when & then
		mockMvc.perform(
				get(PATH + "/{postId}", communityId, memoryId, postId)
					.with(withJwt(1L, 2L, 2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(5006))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

	@Test
	@DisplayName("포스트 목록 조회")
	void searchAllTest() throws Exception {
		// given
		Long communityId = 1L;
		Long memoryId = 1L;

		Pageable pageable = PageRequest.of(0, 10);

		when(postService.searchAll(anyLong(), anyLong(), anyLong(), anyInt(), anyLong())).thenReturn(null);

		// when & then
		mockMvc.perform(
				get(PATH, communityId, memoryId)
					.with(withJwt(1L, 2L, 1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("다른 커뮤니티는 목록 조회를 할 수 없다")
	void searchAllWithDifferentCommunityTest() throws Exception {
		// given
		Long communityId = 1L;
		Long memoryId = 1L;

		Pageable pageable = PageRequest.of(0, 10);

		when(postService.searchAll(anyLong(), anyLong(), anyLong(), anyInt(), anyLong())).thenReturn(null);

		// when & then
		mockMvc.perform(
				get(PATH, communityId, memoryId)
					.with(withJwt(1L, 2L, 2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(5006))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

	@Test
	@DisplayName("포스트 생성")
	void createTest() throws Exception{
		// given
		Long communityId = 1L;
		Long memoryId = 1L;

		CreatePostRequest request = CreatePostRequest.builder()
			.content("test")
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
					.with(withJwt(1L, 2L, 1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("다른 커뮤니티는 포스트를 생성할 수 없다")
	void createWithDifferentCommunityTest() throws Exception{
		// given
		Long communityId = 1L;
		Long memoryId = 1L;

		CreatePostRequest request = CreatePostRequest.builder()
			.content("test")
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
					.with(withJwt(1L, 2L, 2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(5006))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

	@Test
	@DisplayName("content의 최대 크기는 510입니다")
	void createWithContentTest() throws Exception{
		// given
		Long communityId = 1L;
		Long memoryId = 1L;

		String content = "a".repeat(511);
		CreatePostRequest request = CreatePostRequest.builder()
			.content(content)
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
					.with(withJwt(1L, 2L, 1L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("content"))
			.andExpect(jsonPath("$.errors[0].message").value("content는 최대 크기가 510입니다."));
	}

	@Test
	@DisplayName("포스트 수정")
	void updateTest() throws Exception{
		// given
		Long communityId = 1L;
		Long memoryId = 1L;
		Long postId = 1L;

		UpdatePostRequest request = UpdatePostRequest.builder()
			.content("수정된 내용")
			.oldPictures(new ArrayList<>(List.of("test1.png", "test2.png", "test3.png")))
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
					.with(withJwt(1L, 2L, 1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("다른 커뮤니티는 포스트를 수정할 수 없다")
	void updateWithDifferentCommunityTest() throws Exception{
		// given
		Long communityId = 1L;
		Long memoryId = 1L;
		Long postId = 1L;

		UpdatePostRequest request = UpdatePostRequest.builder()
			.content("수정된 내용")
			.oldPictures(new ArrayList<>(List.of("test1.png", "test2.png", "test3.png")))
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
					.with(withJwt(1L, 2L, 2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(5006))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

	@Test
	@DisplayName("content의 최대 크기는 510입니다")
	void updateWithContentTest() throws Exception{
		// given
		Long communityId = 1L;
		Long memoryId = 1L;
		Long postId = 1L;

		String content = "a".repeat(511);
		UpdatePostRequest request = UpdatePostRequest.builder()
			.content(content)
			.oldPictures(new ArrayList<>(List.of("test1.png", "test2.png", "test3.png")))
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
					.with(withJwt(1L, 2L, 1L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("content"))
			.andExpect(jsonPath("$.errors[0].message").value("content는 최대 크기가 510입니다."));
	}

	@Test
	@DisplayName("포스트 삭제")
	public void deleteTest() throws Exception {
		// given
		Long communityId = 1L;
		Long memoryId = 1L;
		Long postId = 1L;

		doNothing().when(postService).delete(anyLong(), anyLong(), anyLong(), anyLong());

		// when & then
		mockMvc.perform(
				delete(PATH + "/{postId}", communityId, memoryId, postId)
					.with(withJwt(1L, 2L, 1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("포스트 삭제")
	public void deleteWithDifferentCommunityTest() throws Exception {
		// given
		Long communityId = 1L;
		Long memoryId = 1L;
		Long postId = 1L;

		doNothing().when(postService).delete(anyLong(), anyLong(), anyLong(), anyLong());

		// when & then
		mockMvc.perform(
				delete(PATH + "/{postId}", communityId, memoryId, postId)
					.with(withJwt(1L, 2L, 2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(5006))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

}
