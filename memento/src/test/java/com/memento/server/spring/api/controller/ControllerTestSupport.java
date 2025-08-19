package com.memento.server.spring.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memento.server.api.controller.comment.CommentController;
import com.memento.server.api.controller.emoji.EmojiController;
import com.memento.server.api.controller.voice.VoiceController;
import com.memento.server.api.service.comment.CommentService;
import com.memento.server.api.service.emoji.EmojiService;
import com.memento.server.api.service.voice.VoiceService;
import com.memento.server.api.service.auth.jwt.JwtProperties;
import com.memento.server.api.service.auth.jwt.JwtTokenProvider;
import com.memento.server.api.service.auth.jwt.MemberClaim;
import com.memento.server.common.validator.FileValidator;
import com.memento.server.spring.config.TestSecurityConfig;

@WebMvcTest({
	VoiceController.class,
	EmojiController.class,
	CommentController.class,
})
@Import({TestSecurityConfig.class, JwtTokenProvider.class})
@EnableConfigurationProperties(JwtProperties.class)
public abstract class ControllerTestSupport {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockitoBean
	protected VoiceService voiceService;

	@MockitoBean
	protected EmojiService emojiService;

	@MockitoBean
	protected CommentService commentService;

	@Autowired
	protected JwtTokenProvider jwtTokenProvider;

	@MockitoBean
	protected FileValidator fileValidator;

	protected RequestPostProcessor withJwt(Long memberId, Long associateId, Long communityId) {
		String token = createTestToken(memberId, associateId, communityId);
		return request -> {
			request.addHeader("Authorization", "Bearer " + token);
			return request;
		};
	}

	protected String createTestToken(Long memberId, Long associateId, Long communityId) {
		MemberClaim claim = MemberClaim.builder()
			.memberId(memberId)
			.associateId(associateId)
			.communityId(communityId)
			.build();

		return jwtTokenProvider.createToken(claim).accessToken();
	}
}
