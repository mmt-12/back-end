package com.memento.server.spring.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memento.server.api.service.achievement.AchievementService;
import com.memento.server.api.service.auth.jwt.JwtProperties;
import com.memento.server.api.service.auth.jwt.JwtTokenProvider;
import com.memento.server.api.service.community.AssociateService;
import com.memento.server.domain.achievement.AchievementRepository;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.spring.config.TestSecurityConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import({JwtTokenProvider.class})
@EnableConfigurationProperties(JwtProperties.class)
@ActiveProfiles("test")
public abstract class ServiceTestSupport {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected AssociateService associateService;

	@Autowired
	protected AchievementService achievementService;

	@MockitoBean
	protected AchievementRepository achievementRepository;

	@MockitoBean
	protected AssociateRepository associateRepository;
}
