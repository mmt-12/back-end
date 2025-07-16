package com.memento.server.spring.docs.hello;

import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.HelloController;
import com.memento.server.HelloRequest;
import com.memento.server.spring.RestDocsSupport;

public class HelloControllerDocsTest extends RestDocsSupport {

	@Override
	protected Object initController() {
		return new HelloController();
	}

	@Test
	@DisplayName("테스트 API")
	void helloTest() throws Exception {
		// given
		HelloRequest request = HelloRequest.builder()
			.id(1L)
			.price(100)
			.name("hello")
			.build();

		// when && then
		mockMvc.perform(
				post("/api/v1/hello")
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("hello-test",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("id").type(NUMBER).description("아이디"),
					fieldWithPath("price").type(NUMBER).description("가격"),
					fieldWithPath("name").type(STRING).description("이름")
				),
				responseFields(
					fieldWithPath("id").type(NUMBER).description("아이디"),
					fieldWithPath("price").type(NUMBER).description("가격"),
					fieldWithPath("name").type(STRING).description("이름")
				)
			));
	}
}
