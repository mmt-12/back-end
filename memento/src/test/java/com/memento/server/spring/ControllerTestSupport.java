package com.memento.server.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memento.server.api.controller.emoji.EmojiController;
import com.memento.server.api.controller.voice.VoiceController;
import com.memento.server.api.service.emoji.EmojiService;
import com.memento.server.api.service.voice.VoiceService;

@WebMvcTest({
	VoiceController.class,
	EmojiController.class,
})
public abstract class ControllerTestSupport {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockitoBean
	protected VoiceService voiceService;

	@MockitoBean
	protected EmojiService emojiService;
}
