package com.memento.server.spring.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memento.server.api.controller.achievement.AchievementController;
import com.memento.server.api.controller.comment.CommentController;
import com.memento.server.api.controller.community.AssociateController;
import com.memento.server.api.controller.emoji.EmojiController;
import com.memento.server.api.controller.guestBook.GuestBookController;
import com.memento.server.api.controller.mbti.MbtiController;
import com.memento.server.api.controller.post.PostController;
import com.memento.server.api.controller.profileImage.ProfileImageController;
import com.memento.server.api.controller.notification.NotificationController;
import com.memento.server.api.controller.voice.VoiceController;
import com.memento.server.api.service.achievement.AchievementService;
import com.memento.server.api.service.comment.CommentService;
import com.memento.server.api.service.community.AssociateService;
import com.memento.server.api.service.emoji.EmojiService;
import com.memento.server.api.service.guestBook.GuestBookService;
import com.memento.server.api.service.mbti.MbtiService;
import com.memento.server.api.service.member.MemberService;
import com.memento.server.api.service.post.PostService;
import com.memento.server.api.service.profileImage.ProfileImageService;
import com.memento.server.api.service.notification.NotificationService;

import com.memento.server.api.service.voice.VoiceService;
import com.memento.server.api.service.auth.jwt.JwtProperties;
import com.memento.server.api.service.auth.jwt.JwtTokenProvider;
import com.memento.server.api.service.auth.jwt.MemberClaim;
import com.memento.server.spring.config.TestSecurityConfig;

@WebMvcTest({
	VoiceController.class,
	EmojiController.class,
	CommentController.class,
	AchievementController.class,
	AssociateController.class,
	GuestBookController.class,
	MbtiController.class,
	ProfileImageController.class,
	NotificationController.class,
	PostController.class,
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
	protected AchievementService achievementService;

	@MockitoBean
	protected AssociateService associateService;

	@MockitoBean
	protected CommentService commentService;

	@MockitoBean
	protected GuestBookService guestBookService;

	@MockitoBean
	protected MbtiService mbtiService;

	@MockitoBean
	protected ProfileImageService profileImageService;

	@MockitoBean
	protected MemberService memberService;

	@MockitoBean
	protected PostService postService;
  
  @MockitoBean
  protected NotificationService notificationService;

	@Autowired
	protected JwtTokenProvider jwtTokenProvider;

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
