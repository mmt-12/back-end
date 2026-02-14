package com.memento.server.api.service.email;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

	private EmailService emailService;

	@Mock
	private JavaMailSender mailSender;

	@Mock
	private TemplateEngine templateEngine;

	@Mock
	private MimeMessage mimeMessage;

	@BeforeEach
	void setUp() {
		emailService = new EmailService(mailSender, templateEngine);
		ReflectionTestUtils.setField(emailService, "adminEmail", "admin@test.com");
		ReflectionTestUtils.setField(emailService, "baseUrl", "http://localhost:8080");
	}

	@Test
	@DisplayName("회원가입 요청 이메일을 전송한다.")
	void sendSignupRequestEmail() {
		// given
		Long memberId = 1L;
		String name = "홍길동";
		String email = "hong@test.com";
		LocalDate birthday = LocalDate.of(1990, 1, 1);
		String token = "test-token-123";

		when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
		when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("<html>test</html>");

		// when
		emailService.sendSignupRequestEmail(memberId, name, email, birthday, token);

		// then
		verify(mailSender, times(1)).createMimeMessage();
		verify(mailSender, times(1)).send(any(MimeMessage.class));
		verify(templateEngine, times(1)).process(any(String.class), any(Context.class));
	}
}
