package com.memento.server.spring.api.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.hello.HelloRequest;
import com.memento.server.spring.ControllerTestSupport;
import com.memento.server.spring.WithMockCustomUser;

class HelloControllerTest extends ControllerTestSupport {

	@Test
	@DisplayName("테스트 API")
	@WithMockCustomUser
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
			.andExpect(jsonPath("$.id").value(request.id()))
			.andExpect(jsonPath("$.price").value(request.price() + 100))
			.andExpect(jsonPath("$.name").value(request.name()));
	}
}
