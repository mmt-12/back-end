package com.memento.server.docs.profileImage;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
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

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.profileImage.ProfileImageController;
import com.memento.server.api.controller.profileImage.dto.SearchProfileImageResponse;
import com.memento.server.api.service.profileImage.ProfileImageService;
import com.memento.server.docs.RestDocsSupport;

public class ProfileImageControllerDocsTest extends RestDocsSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/associates/{associateId}/profile-images";

	private final ProfileImageService profileImageService = mock(ProfileImageService.class);

	@Override
	protected Object initController() {
		return new ProfileImageController(profileImageService);
	}

	@Test
	@DisplayName("프로필 이미지 등록")
	void createTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		Long communityId = 1L;
		Long associateId = 2L;

		MockMultipartFile file = new MockMultipartFile(
			"image",
			"image.png",
			"image/png",
			"sample image content".getBytes()
		);

		doNothing().when(profileImageService).create(anyLong(), anyLong(), anyLong(), eq(file));

		// when & then
		mockMvc.perform(
				multipart(PATH, communityId, associateId)
					.file(file)
					.contentType(MULTIPART_FORM_DATA)
					.with(request -> {
						request.setMethod("POST");     // 명시적 POST 설정
						return request;
					})
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("profileImage-create-test",
				preprocessRequest(prettyPrint()),
				requestParts(
					partWithName("image").description("이미지 파일")
				)
			));
	}

	@Test
	@DisplayName("프로필 이미지 등록 취소")
	void deleteTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		Long communityId = 1L;
		Long associateId = 1L;
		Long profileImageId = 1L;

		doNothing().when(profileImageService).delete(anyLong(), anyLong(), anyLong(), anyLong());

		// when & then
		mockMvc.perform(
				delete(PATH + "/{profileImageId}", communityId, associateId, profileImageId))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("profileImage-delete-test"));
	}

	@Test
	@DisplayName("프로필 이미지 조회")
	void readTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		Long groupId = 1L;
		Long associateId = 1L;

		int size = 10;
		Long cursor = 0L;

		SearchProfileImageResponse response = SearchProfileImageResponse.builder()
			.profileImages(List.of(
				SearchProfileImageResponse.ProfileImage.builder()
					.id(1L)
					.url("www.example.com/s3/seonwoo/1")
					.build()
			))
			.cursor(2L)
			.hasNext(false)
			.build();

		Pageable pageable = PageRequest.of(0, size);

		when(profileImageService.search(anyLong(), anyLong(), eq(pageable), anyLong())).thenReturn(response);

		// when & then
		mockMvc.perform(
				get(PATH, groupId, associateId)
					.param("size", String.valueOf(size))
					.param("cursor", String.valueOf(cursor)))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("profileImage-read-test",
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("profileImages").type(ARRAY).description("프로필 이미지 리스트"),
					fieldWithPath("profileImages[].id").type(NUMBER).description("프로필 이미지 ID"),
					fieldWithPath("profileImages[].url").type(STRING).description("프로필 이미지 경로"),
					fieldWithPath("cursor").type(NUMBER).description("커서 값"),
					fieldWithPath("hasNext").type(BOOLEAN).description("다음 값 존재 여부")
				)
			));

	}
}
