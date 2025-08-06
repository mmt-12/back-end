package com.memento.server.docs;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.memento.server.config.argumentResolver.AssociateIdArgumentResolver;
import com.memento.server.config.argumentResolver.CommunityIdArgumentResolver;
import com.memento.server.config.argumentResolver.MemberIdArgumentResolver;
import com.memento.server.api.service.auth.MemberPrincipal;

@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsSupport {

	protected MockMvc mockMvc;
	protected ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

	protected abstract Object initController();

	@BeforeEach
	void setUp(RestDocumentationContextProvider provider) {
		this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
			.setCustomArgumentResolvers(
				new MemberIdArgumentResolver(),
				new CommunityIdArgumentResolver(),
				new AssociateIdArgumentResolver())
			.apply(documentationConfiguration(provider))
			.build();
	}

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	protected void setAuthentication(Long memberId, Long associateId, Long communityId) {
		MemberPrincipal principal = new MemberPrincipal(memberId, associateId, communityId);
		Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
}