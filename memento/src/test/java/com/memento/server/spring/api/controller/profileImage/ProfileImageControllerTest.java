package com.memento.server.spring.api.controller.profileImage;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.profileImage.dto.SearchProfileImageResponse;
import com.memento.server.spring.api.controller.ControllerTestSupport;

public class ProfileImageControllerTest extends ControllerTestSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/associates/{associateId}/profile-images";

	@Test
	@DisplayName("프로필 이미지 등록")
	void createTest() throws Exception {
		// given
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
						request.setMethod("POST");
						return request;
					})
					.with(withJwt(1L,1L,1L))
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("다른 그룹의 참여자는 프로필 이미지를 등록할 수 없습니다")
	void createWithDifferentCommunityTest() throws Exception {
		// given
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
						request.setMethod("POST");
						return request;
					})
					.with(withJwt(1L,1L,2L))
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(7005))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

	@Test
	@DisplayName("자기 자신의 프로필 이미지는 등록할 수 없습니다")
	void createWithSameAssociateTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;

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
						request.setMethod("POST");
						return request;
					})
					.with(withJwt(1L,1L,1L))
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(7006))
			.andExpect(jsonPath("$.message").value("권한이 없는 참여자입니다."));
	}

	@Test
	@DisplayName("이미지는 비어있을 수 없습니다")
	void createWithNullImageTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 2L;

		MockMultipartFile file = new MockMultipartFile(
			"image",
			"",
			"image/png",
			"sample image content".getBytes()
		);

		doNothing().when(profileImageService).create(anyLong(), anyLong(), anyLong(), eq(file));

		// when & then
		mockMvc.perform(
				multipart(PATH, communityId, associateId)
					.contentType(MULTIPART_FORM_DATA)
					.with(request -> {
						request.setMethod("POST");
						return request;
					})
					.with(withJwt(1L,1L,1L))
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(1002))
			.andExpect(jsonPath("$.message").value("필수 요청 part 누락"));
	}

	@Test
	@DisplayName("프로필 이미지 등록 취소")
	void deleteTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;
		Long profileImageId = 1L;

		doNothing().when(profileImageService).delete(anyLong(), anyLong(), anyLong(), anyLong());

		// when & then
		mockMvc.perform(
				delete(PATH + "/{profileImageId}", communityId, associateId, profileImageId)
					.with(withJwt(1L,1L,1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("다른 그룹의 참여자는 삭제할 수 없습니다")
	void deleteWithDifferentCommunityTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;
		Long profileImageId = 1L;

		doNothing().when(profileImageService).delete(anyLong(), anyLong(), anyLong(), anyLong());

		// when & then
		mockMvc.perform(
				delete(PATH + "/{profileImageId}", communityId, associateId, profileImageId)
					.with(withJwt(1L,1L,2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(7005))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

	@Test
	@DisplayName("프로필 이미지 조회")
	void readTest() throws Exception {
		// given
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
					.param("cursor", String.valueOf(cursor))
					.with(withJwt(1L,1L,1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("다른 그룹 참여자는 조회할 수 없습니다")
	void readWithDifferentCommunityTest() throws Exception {
		// given
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
					.param("cursor", String.valueOf(cursor))
					.with(withJwt(1L,1L,2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(7005))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}
}
