package com.memento.server.spring.api.controller.guestBook;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.guestBook.dto.request.CreateGuestBookRequest;
import com.memento.server.api.service.guestBook.dto.response.SearchGuestBookResponse;
import com.memento.server.domain.guestBook.GuestBookType;
import com.memento.server.spring.api.controller.ControllerTestSupport;

public class GuestBookControllerTest extends ControllerTestSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/associates/{associateId}/guest-books";

	@Test
	@DisplayName("텍스트 방명록 생성")
	void createTextTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;

		CreateGuestBookRequest request = CreateGuestBookRequest.builder()
			.type(GuestBookType.TEXT)
			.contentId(null)
			.content("example")
			.build();

		doNothing().when(guestBookService).create(anyLong(), anyLong(), anyLong(), eq(GuestBookType.TEXT), isNull(), anyString());

		// when & then
		mockMvc.perform(
				post(PATH, communityId, associateId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON)
					.with(withJwt(1L, 2L, 1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("리액션 방명록 생성")
	void createReactionTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;

		CreateGuestBookRequest request = CreateGuestBookRequest.builder()
			.type(GuestBookType.EMOJI)
			.contentId(1L)
			.content(null)
			.build();

		doNothing().when(guestBookService).create(anyLong(), anyLong(), anyLong(), eq(GuestBookType.EMOJI), anyLong(), isNull());

		// when & then
		mockMvc.perform(
				post(PATH, communityId, associateId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON)
					.with(withJwt(1L, 2L, 1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("같은 그룹의 참여자만 작성 가능합니다")
	void createTextDifferentCommunityTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;

		CreateGuestBookRequest request = CreateGuestBookRequest.builder()
			.type(GuestBookType.TEXT)
			.contentId(null)
			.content("example")
			.build();

		doNothing().when(guestBookService).create(anyLong(), anyLong(), anyLong(), eq(GuestBookType.TEXT), isNull(), anyString());

		// when & then
		mockMvc.perform(
				post(PATH, communityId, associateId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON)
					.with(withJwt(1L, 1L, 2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(5006))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

	@Test
	@DisplayName("content의 최대 크기는 255입니다")
	void createTextWithContentTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;

		CreateGuestBookRequest request = CreateGuestBookRequest.builder()
			.type(GuestBookType.TEXT)
			.contentId(null)
			.content("a".repeat(256))
			.build();

		doNothing().when(guestBookService).create(anyLong(), anyLong(), anyLong(), eq(GuestBookType.TEXT), isNull(), anyString());

		// when & then
		mockMvc.perform(
				post(PATH, communityId, associateId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON)
					.with(withJwt(1L, 1L, 1L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("content"))
			.andExpect(jsonPath("$.errors[0].message").value("content는 최대 크기가 255입니다."));
	}

	@Test
	@DisplayName("방명록 일회용 보이스 생성")
	void createBubbleTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;

		MockMultipartFile file = new MockMultipartFile(
			"voice",
			"voice.mp3",
			"audio/mpeg",
			"sample voice content".getBytes()
		);

		// when & then
		mockMvc.perform(
				multipart(PATH + "/bubble", communityId, associateId)
					.file(file)
					.contentType(MULTIPART_FORM_DATA)
					.with(request -> {
						request.setMethod("POST");
						return request;
					})
					.with(withJwt(1L, 2L, 1L))
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("같은 그룹의 참여자만 작성 가능합니다")
	void createBubbleDifferentCommunityTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;

		MockMultipartFile file = new MockMultipartFile(
			"voice",
			"voice.mp3",
			"audio/mpeg",
			"sample voice content".getBytes()
		);

		// when & then
		mockMvc.perform(
				multipart(PATH + "/bubble", communityId, associateId)
					.file(file)
					.contentType(MULTIPART_FORM_DATA)
					.with(request -> {
						request.setMethod("POST");
						return request;
					})
					.with(withJwt(1L, 1L, 2L))
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(5006))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

	@Test
	@DisplayName("방명록 조회")
	void searchTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;
		int size = 10;
		Long cursor = 100L;
		Pageable pageable = PageRequest.of(0, size);

		SearchGuestBookResponse response = SearchGuestBookResponse.builder()
			.guestBooks(List.of(SearchGuestBookResponse.GuestBook.builder()
				.id(1L)
				.type(GuestBookType.TEXT)
				.content("example")
				.createdAt(LocalDateTime.now())
				.build()))
			.nextCursor(cursor)
			.hasNext(true)
			.build();

		when(guestBookService.search(anyLong(), anyLong(), anyInt(), anyLong())).thenReturn(response);

		// when & then
		mockMvc.perform(
				get(PATH, communityId, associateId)
					.param("size", String.valueOf(size))
					.param("cursor", String.valueOf(cursor))
					.with(withJwt(1L, 1L, 1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("같은 그룹의 참여자만 조회 가능합니다")
	void searchWithDifferentCommunityTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;
		int size = 10;
		Long cursor = 100L;
		Pageable pageable = PageRequest.of(0, size);

		SearchGuestBookResponse response = SearchGuestBookResponse.builder()
			.guestBooks(List.of(SearchGuestBookResponse.GuestBook.builder()
				.id(1L)
				.type(GuestBookType.TEXT)
				.content("example")
				.createdAt(LocalDateTime.now())
				.build()))
			.nextCursor(cursor)
			.hasNext(true)
			.build();

		when(guestBookService.search(anyLong(), anyLong(), anyInt(), anyLong())).thenReturn(response);

		// when & then
		mockMvc.perform(
				get(PATH, communityId, associateId)
					.param("size", String.valueOf(size))
					.param("cursor", String.valueOf(cursor))
					.with(withJwt(1L, 1L, 2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(5006))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

	@Test
	@DisplayName("방명록 삭제")
	void deleteTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;
		Long guestBookId = 101L;

		doNothing().when(guestBookService).delete(guestBookId);

		// when & then
		mockMvc.perform(
				delete(PATH + "/{guestBookId}", communityId, associateId, guestBookId)
					.with(withJwt(1L, 1L, 1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("다른 그룹의 참여자는 삭제할 수 없습니다")
	void deleteWithDifferentCommunityTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;
		Long guestBookId = 101L;

		doNothing().when(guestBookService).delete(guestBookId);

		// when & then
		mockMvc.perform(
				delete(PATH + "/{guestBookId}", communityId, associateId, guestBookId)
					.with(withJwt(1L, 1L, 2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(5006))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

	@Test
	@DisplayName("자신의 방명록만 삭제할 수 있다")
	void deleteWithDifferentAssociateTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;
		Long guestBookId = 101L;

		doNothing().when(guestBookService).delete(guestBookId);

		// when & then
		mockMvc.perform(
				delete(PATH + "/{guestBookId}", communityId, associateId, guestBookId)
					.with(withJwt(1L, 2L, 1L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(7005))
			.andExpect(jsonPath("$.message").value("권한이 없는 참여자입니다."));
	}
}
