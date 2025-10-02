package com.memento.server.docs.memory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.memento.server.api.controller.memory.MemoryController;
import com.memento.server.api.controller.memory.dto.CreateUpdateMemoryRequest;
import com.memento.server.api.controller.memory.dto.CreateUpdateMemoryResponse;
import com.memento.server.api.controller.memory.dto.DownloadImagesResponse;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse.MemoryResponse;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse.MemoryResponse.LocationResponse;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse.MemoryResponse.PeriodResponse;
import com.memento.server.api.controller.memory.dto.ReadMemoryResponse;
import com.memento.server.api.service.memory.MemoryService;
import com.memento.server.docs.RestDocsSupport;

public class MemoryControllerDocsTest extends RestDocsSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/memories";

	private final MemoryService memoryService = mock(MemoryService.class);

	@Override
	protected Object initController() {
		return new MemoryController(memoryService);
	}

	@Test
	@DisplayName("기억 상세 조회")
	void read() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);
		when(memoryService.read(any())).thenReturn(
			ReadMemoryResponse.builder()
				.id(1L)
				.title("일평 mt")
				.description("우리가 함께 마신 소주와 수영장 물을 추억하며")
				.period(ReadMemoryResponse.PeriodResponse.builder()
					.startTime(LocalDateTime.of(2025, 6, 20, 10, 30))
					.endTime(LocalDateTime.of(2025, 6, 21, 12, 30))
					.build())
				.location(ReadMemoryResponse.LocationResponse.builder()
					.latitude(36.34512323F)
					.longitude(138.7712322F)
					.code(16335)
					.name("양평 서종풀팬션")
					.address("경기도 양평시 양평군")
					.build()
				)
				.memberAmount(22)
				.pictureAmount(64)
				.pictures(List.of(
					"https://aws.s3.memento/1",
					"https://aws.s3.memento/2",
					"https://aws.s3.memento/3",
					"https://aws.s3.memento/4",
					"https://aws.s3.memento/5",
					"https://aws.s3.memento/6",
					"https://aws.s3.memento/7",
					"https://aws.s3.memento/8",
					"https://aws.s3.memento/9"
				))
				.author(ReadMemoryResponse.AuthorResponse.builder()
					.id(1L)
					.nickname("nickname")
					.imageUrl("https://aws.s3.memento/1")
					.achievement(ReadMemoryResponse.AuthorResponse.AchievementResponse.builder()
						.id(1L)
						.name("achievement")
						.build())
					.build())
				.build()
		);

		// when & then
		mockMvc.perform(get(PATH + "/{memoryId}", 1L, 1L))
			.andExpect(status().isOk())
			.andDo(document("memory-read-test",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("그룹 아이디"),
					parameterWithName("memoryId").description("기억 아이디")
				),
				responseFields(
					fieldWithPath("id").description("기억 아이디"),
					fieldWithPath("title").description("기억 이름"),
					fieldWithPath("description").description("기억 설명"),
					subsectionWithPath("period").description("기억 기간 정보"),
					fieldWithPath("period.startTime").description("기억 시작 시간"),
					fieldWithPath("period.endTime").description("기억 끝 시간"),
					subsectionWithPath("location").description("기억 장소 정보"),
					fieldWithPath("location.latitude").description("장소 - 위도"),
					fieldWithPath("location.longitude").description("장소 - 경도"),
					fieldWithPath("location.code").description("장소 우편번호"),
					fieldWithPath("location.name").description("장소 이름"),
					fieldWithPath("location.address").description("장소 주소"),
					fieldWithPath("memberAmount").description("기억 참여자 수"),
					fieldWithPath("pictureAmount").description("기억 사진 수"),
					fieldWithPath("pictures").description("사진 url 목록"),
					subsectionWithPath("author").description("작성자 정보"),
					fieldWithPath("author.id").description("작성자의 참여 아이디"),
					fieldWithPath("author.nickname").description("작성자 닉네임"),
					subsectionWithPath("author.achievement").description("작성자 업적"),
					fieldWithPath("author.achievement.id").description("업적 아이디"),
					fieldWithPath("author.achievement.name").description("업적 이름")
				)
			));
	}

	@Test
	@DisplayName("기억 목록 조회")
	void readAll() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);
		when(memoryService.readAll(any(), any())).thenReturn(
			ReadAllMemoryResponse.builder()
				.nextCursor(3L)
				.hasNext(true)
				.memories(
					List.of(
						MemoryResponse.builder()
							.id(1L)
							.title("일평 mt")
							.description("우리가 함께 마신 소주와 수영장 물을 추억하며")
							.period(PeriodResponse.builder()
								.startTime(LocalDateTime.of(2025, 6, 20, 10, 30))
								.endTime(LocalDateTime.of(2025, 6, 21, 12, 30))
								.build())
							.location(LocationResponse.builder()
								.latitude(36.34512323F)
								.longitude(138.7712322F)
								.code(16335)
								.name("양평 서종풀팬션")
								.address("경기도 양평시 양평군")
								.build()
							)
							.memberAmount(22)
							.pictureAmount(64)
							.pictures(List.of(
								"https://aws.s3.memento/1",
								"https://aws.s3.memento/2",
								"https://aws.s3.memento/3",
								"https://aws.s3.memento/4",
								"https://aws.s3.memento/5",
								"https://aws.s3.memento/6",
								"https://aws.s3.memento/7",
								"https://aws.s3.memento/8",
								"https://aws.s3.memento/9"
							))
							.author(ReadAllMemoryResponse.MemoryResponse.AuthorResponse.builder()
								.id(1L)
								.nickname("nickname")
								.imageUrl("https://aws.s3.memento/1")
								.achievement(ReadAllMemoryResponse.MemoryResponse.AuthorResponse.AchievementResponse.builder()
									.id(1L)
									.name("achievement")
									.build())
								.build())
							.build()
					)
				)
				.build()
		);

		// when & then
		mockMvc.perform(get(PATH, 1L)
				.param("cursor", "")
				.param("size", "")
				.param("keyword", "")
				.param("startTime", "2025-08-06")
				.param("endTime", "2025-08-06"))
			.andExpect(status().isOk())
			.andDo(document("memory-read-all-test",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("그룹 아이디")
				),
				queryParameters(
					parameterWithName("cursor").description("커서값"),
					parameterWithName("size").description("검색 크기"),
					parameterWithName("keyword").description("키워드"),
					parameterWithName("startTime").description("검색 범위 시작 날짜 (YYYY-MM-DD)"),
					parameterWithName("endTime").description("검색 범위 끝 날짜 (YYYY-MM-DD)")
				),
				responseFields(
					fieldWithPath("nextCursor").description("다음 커서 값"),
					fieldWithPath("hasNext").description("다음 데이터 여부"),
					subsectionWithPath("memories").description("기억 목록"),
					fieldWithPath("memories[].id").description("기억 아이디"),
					fieldWithPath("memories[].title").description("기억 이름"),
					fieldWithPath("memories[].description").description("기억 설명"),
					subsectionWithPath("memories[].period").description("기억 기간 정보"),
					fieldWithPath("memories[].period.startTime").description("기억 시작 시간"),
					fieldWithPath("memories[].period.endTime").description("기억 끝 시간"),
					subsectionWithPath("memories[].location").description("기억 장소 정보"),
					fieldWithPath("memories[].location.latitude").description("장소 - 위도"),
					fieldWithPath("memories[].location.longitude").description("장소 - 경도"),
					fieldWithPath("memories[].location.code").description("장소 우편번호"),
					fieldWithPath("memories[].location.name").description("장소 이름"),
					fieldWithPath("memories[].location.address").description("장소 주소"),
					fieldWithPath("memories[].memberAmount").description("기억 참여자 수"),
					fieldWithPath("memories[].pictureAmount").description("기억 사진 수"),
					fieldWithPath("memories[].pictures").description("사진 url 목록"),
					subsectionWithPath("memories[].author").description("작성자 정보"),
					fieldWithPath("memories[].author.id").description("작성자의 참여 아이디"),
					fieldWithPath("memories[].author.nickname").description("작성자 닉네임"),
					subsectionWithPath("memories[].author.achievement").description("작성자 업적"),
					fieldWithPath("memories[].author.achievement.id").description("업적 아이디"),
					fieldWithPath("memories[].author.achievement.name").description("업적 이름")
				)
			));
	}

	@Test
	@DisplayName("기억 생성")
	void create() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);
		CreateUpdateMemoryRequest request = CreateUpdateMemoryRequest.builder()
			.title("양평 MT!")
			.period(CreateUpdateMemoryRequest.PeriodRequest.builder()
				.startTime(LocalDateTime.of(2024, 6, 20, 10, 30, 0))
				.endTime(LocalDateTime.of(2024, 6, 20, 10, 30, 0))
				.build())
			.description("우리가 함께 마신 소주와 수영장 물을 기억하며")
			.associates(List.of(1L, 2L, 3L, 4L))
			.location(CreateUpdateMemoryRequest.LocationRequest.builder()
				.latitude(36.34512323f)
				.longitude(138.7712322f)
				.code(16335)
				.name("양평 서종풀팬션")
				.address("경기도 양평시 양평군")
				.build())
			.build();
		when(memoryService.create(any(), any(), any())).thenReturn(
			CreateUpdateMemoryResponse.builder()
				.memoryId(1L)
				.build()
		);

		// when & then
		mockMvc.perform(post(PATH, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andDo(document("memory-create-test",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("그룹 아이디")
				),
				requestFields(
					fieldWithPath("title").description("제목"),
					subsectionWithPath("period").description("기간"),
					fieldWithPath("period.startTime").description("시작 시간"),
					fieldWithPath("period.endTime").description("종료 시간"),
					fieldWithPath("description").description("설명"),
					fieldWithPath("associates").description("참여자 아이디 목록"),
					subsectionWithPath("location").description("지역"),
					fieldWithPath("location.address").description("주소"),
					fieldWithPath("location.name").description("장소 이름"),
					fieldWithPath("location.latitude").description("위도 (소수점 포함 총 10자리 숫자, 소수점 아래 7자리까지)"),
					fieldWithPath("location.longitude").description("경도 (소수점 포함 총 10자리 숫자, 소수점 아래 7자리까지)"),
					fieldWithPath("location.code").description("지도 API의 지점 코드 (지점 아이디)")
				),
				responseFields(
					fieldWithPath("memoryId").description("생성된 기억의 아이디")
				)
			));
	}

	@Test
	@DisplayName("기억 수정")
	void update() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);
		CreateUpdateMemoryRequest request = CreateUpdateMemoryRequest.builder()
			.title("양평 MT!")
			.period(CreateUpdateMemoryRequest.PeriodRequest.builder()
				.startTime(LocalDateTime.of(2024, 6, 20, 10, 30, 0))
				.endTime(LocalDateTime.of(2024, 6, 20, 10, 30, 0))
				.build())
			.description("우리가 함께 마신 소주와 수영장 물을 기억하며")
			.associates(List.of(1L, 2L, 3L, 4L))
			.location(CreateUpdateMemoryRequest.LocationRequest.builder()
				.latitude(36.34512323f)
				.longitude(138.7712322f)
				.code(16335)
				.name("양평 서종풀팬션")
				.address("경기도 양평시 양평군")
				.build())
			.build();
		when(memoryService.update(any(), any(), any())).thenReturn(
			CreateUpdateMemoryResponse.builder()
				.memoryId(1L)
				.build()
		);

		// when & then
		mockMvc.perform(put(PATH + "/{memoryId}", 1L, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andDo(document("memory-update-test",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("그룹 아이디"),
					parameterWithName("memoryId").description("기억 아이디")
				),
				requestFields(
					fieldWithPath("title").description("제목"),
					subsectionWithPath("period").description("기간"),
					fieldWithPath("period.startTime").description("시작 시간"),
					fieldWithPath("period.endTime").description("종료 시간"),
					fieldWithPath("description").description("설명"),
					fieldWithPath("associates").description("참여자 아이디 목록"),
					subsectionWithPath("location").description("지역"),
					fieldWithPath("location.address").description("주소"),
					fieldWithPath("location.name").description("장소 이름"),
					fieldWithPath("location.latitude").description("위도 (소수점 포함 총 10자리 숫자, 소수점 아래 7자리까지)"),
					fieldWithPath("location.longitude").description("경도 (소수점 포함 총 10자리 숫자, 소수점 아래 7자리까지)"),
					fieldWithPath("location.code").description("지도 API의 지점 코드 (지점 아이디)")
				),
				responseFields(
					fieldWithPath("memoryId").description("생성된 기억의 아이디")
				)
			));
	}

	@Test
	@DisplayName("기억 삭제")
	void deleteMemory() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		// when & then
		mockMvc.perform(delete(PATH + "/{memoryId}", 1L, 1L)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("memory-delete-test",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("그룹 아이디"),
					parameterWithName("memoryId").description("기억 아이디")
				)
			));
	}

	@Test
	@DisplayName("기억 이미지 다운로드")
	void downloadImages() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);
		when(memoryService.downloadImages(any())).thenReturn(
			DownloadImagesResponse.builder()
				.pictures(List.of(
					"www.example.com/aws/s3/seonwoo/1",
					"www.example.com/aws/s3/seonwoo/2",
					"www.example.com/aws/s3/seonwoo/3",
					"www.example.com/aws/s3/seonwoo/4",
					"www.example.com/aws/s3/seonwoo/5",
					"www.example.com/aws/s3/seonwoo/6",
					"www.example.com/aws/s3/seonwoo/7"
				))
				.build()
		);

		// when & then
		mockMvc.perform(get(PATH + "/{memoryId}/images", 1L, 1L))
			.andExpect(status().isOk())
			.andDo(document("memory-download-test",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("그룹 아이디"),
					parameterWithName("memoryId").description("기억 아이디")
				),
				responseFields(
					fieldWithPath("pictures").description("해당 기억에 업로드된 모든 이미지 경로")
				)
			));
	}
}
