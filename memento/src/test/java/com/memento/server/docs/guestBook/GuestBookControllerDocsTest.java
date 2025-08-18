package com.memento.server.docs.guestBook;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.guestBook.GuestBookController;
import com.memento.server.api.controller.guestBook.dto.CreateGuestBookRequest;
import com.memento.server.api.controller.guestBook.dto.SearchGuestBookResponse;
import com.memento.server.api.service.guestBook.GuestBookService;
import com.memento.server.docs.RestDocsSupport;
import com.memento.server.domain.guestBook.GuestBookType;

public class GuestBookControllerDocsTest extends RestDocsSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/associates/{associateId}/guest-books";
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	private final GuestBookService guestBookService = mock(GuestBookService.class);

	@Override
	protected Object initController() {
		return new GuestBookController(guestBookService);
	}

	@Test
	@DisplayName("텍스트 방명록 생성")
	void createTextTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		Long communityId = 1L;
		Long associateId = 1L;

		CreateGuestBookRequest request = CreateGuestBookRequest.builder()
			.type(GuestBookType.TEXT)
			.contentId(null)
			.content("example")
			.build();

		doNothing().when(guestBookService).create(anyLong(), anyLong(), eq(GuestBookType.TEXT), isNull(), anyString());

		// when & then
		mockMvc.perform(
				post(PATH, communityId, associateId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("guestBook-create-text-test",
				preprocessRequest(prettyPrint()),
				requestFields(
					fieldWithPath("type").type(STRING).description("방명록 타입"),
					fieldWithPath("contentId").type(NULL).description("리액션 아이디"),
					fieldWithPath("content").type(STRING).description("내용")
				)
			));

	}

	@Test
	@DisplayName("리액션 방명록 생성")
	void createReactionTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		Long communityId = 1L;
		Long associateId = 1L;

		CreateGuestBookRequest request = CreateGuestBookRequest.builder()
			.type(GuestBookType.EMOJI)
			.contentId(1L)
			.content(null)
			.build();

		doNothing().when(guestBookService).create(anyLong(), anyLong(), eq(GuestBookType.EMOJI), anyLong(), isNull());

		// when & then
		mockMvc.perform(
				post(PATH, communityId, associateId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("guestBook-create-reaction-test",
				preprocessRequest(prettyPrint()),
				requestFields(
					fieldWithPath("type").type(STRING).description("방명록 타입"),
					fieldWithPath("contentId").type(NUMBER).description("리액션 아이디"),
					fieldWithPath("content").type(NULL).description("내용")
				)
			));

	}

	@Test
	@DisplayName("방명록 일회용 보이스 생성")
	void createBubbleTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

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
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("guestBook-create-bubble-test",
				preprocessRequest(prettyPrint()),
				requestParts(
					partWithName("voice").description("음성 파일")
				)
			));
	}

	@Test
	@DisplayName("방명록 조회")
	void searchTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

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
			.cursor(cursor)
			.hasNext(true)
			.build();

		when(guestBookService.search(anyLong(), anyLong(), eq(pageable), anyLong())).thenReturn(response);

		// when & then
		mockMvc.perform(
				get(PATH, communityId, associateId)
					.param("size", String.valueOf(size))
					.param("cursor", String.valueOf(cursor)))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("guestBook-read-test",
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("guestBooks").type(ARRAY).description("방명록 리스트"),
					fieldWithPath("guestBooks[].id").type(NUMBER).description("방명록 ID"),
					fieldWithPath("guestBooks[].type").type(STRING).description("방명록 종류"),
					fieldWithPath("guestBooks[].content").type(STRING).description("방명록 내용"),
					fieldWithPath("guestBooks[].createdAt").type(STRING).description("방명록 생성 시각"),
					fieldWithPath("cursor").type(NUMBER).description("커서 값"),
					fieldWithPath("hasNext").type(BOOLEAN).description("다음 값 존재 여부")
				)
			));
	}

	@Test
	@DisplayName("방명록 삭제")
	void deleteTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		Long communityId = 1L;
		Long associateId = 1L;
		Long guestBookId = 101L;

		doNothing().when(guestBookService).delete(guestBookId);

		// when & then
		mockMvc.perform(
				delete(PATH + "/{guestBookId}", communityId, associateId, guestBookId))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("guestBook-delete-test"));
	}
}
