package com.memento.server.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memento.server.api.controller.voice.VoiceController;
import com.memento.server.api.service.voice.VoiceService;
import com.memento.server.hello.HelloController;

@WebMvcTest({
	HelloController.class,
	VoiceController.class,
})
public abstract class ControllerTestSupport {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockitoBean
	protected VoiceService voiceService;
}
