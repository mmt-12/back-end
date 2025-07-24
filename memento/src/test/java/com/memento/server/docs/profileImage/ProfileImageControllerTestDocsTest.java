package com.memento.server.docs.profileImage;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.profileImage.ProfileImageController;
import com.memento.server.api.controller.profileImage.dto.ReadProfileImageResponse;
import com.memento.server.docs.RestDocsSupport;

public class ProfileImageControllerTestDocsTest extends RestDocsSupport {

	public static final String PATH = "/api/v1/groups/{groupId}/associates/{associateId}/profile-images";
	@Override
	protected Object initController() {
		return new ProfileImageController();
	}

	@Test
	@DisplayName("프로필 이미지 등록")
	void createTest() throws Exception{
		// given
		Long groupId = 1L;
		Long associateId = 1L;

		MockMultipartFile file = new MockMultipartFile(
			"image",
			"image.png",
			"image/png",
			"sample image content".getBytes()
		);

		// when & then
		mockMvc.perform(
			multipart(PATH, groupId, associateId)
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
	void deleteTest() throws Exception{
		// given
		Long groupId = 1L;
		Long associateId = 1L;
		Long profileImageId = 1L;

		// when & then
		mockMvc.perform(
				delete(PATH + "/{profileImageId}", groupId, associateId, profileImageId))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("profileImage-delete-test"));
	}

	@Test
	@DisplayName("프로필 이미지 조회")
	void readTest() throws Exception{
		// given
		Long groupId = 1L;
		Long associateId = 1L;

		Long size = 10L;
		Long cursor = 0L;

		// when & then
		ReadProfileImageResponse response = ReadProfileImageResponse.builder()
			.profileImages(List.of(
				ReadProfileImageResponse.ProfileImage.builder()
					.id(1L)
					.url("www.example.com/s3/seonwoo/1")
					.build(),
				ReadProfileImageResponse.ProfileImage.builder()
					.id(2L)
					.url("www.example.com/s3/seonwoo/2")
					.build()
			))
			.cursor(2L)
			.hasNext(false)
			.build();

		mockMvc.perform(
			get(PATH, groupId, associateId)
				.param("size", String.valueOf(size))
				.param("cursor", String.valueOf(cursor)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.profileImages[0].id").value(response.profileImages().get(0).getId()))
			.andExpect(jsonPath("$.profileImages[0].url").value(response.profileImages().get(0).getUrl()))
			.andExpect(jsonPath("$.profileImages[1].id").value(response.profileImages().get(1).getId()))
			.andExpect(jsonPath("$.profileImages[1].url").value(response.profileImages().get(1).getUrl()))
			.andExpect(jsonPath("$.cursor").value(response.cursor()))
			.andExpect(jsonPath("$.hasNext").value(response.hasNext()))
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
