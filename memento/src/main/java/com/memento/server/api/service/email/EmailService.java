package com.memento.server.api.service.email;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;

	@Value("${app.admin-email}")
	private String adminEmail;

	@Value("${app.base-url}")
	private String baseUrl;

	public void sendSignupRequestEmail(Long memberId, String name, String email, LocalDate birthday) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(adminEmail);
			helper.setSubject("[Memento] 새로운 회원가입 요청 - " + name);

			String htmlContent = buildSignupRequestEmailContent(memberId, name, email, birthday);
			helper.setText(htmlContent, true);

			mailSender.send(message);
			log.info("회원가입 요청 이메일 전송 완료 - memberId: {}, name: {}", memberId, name);
		} catch (MessagingException e) {
			log.error("회원가입 요청 이메일 전송 실패 - memberId: {}, error: {}", memberId, e.getMessage());
		}
	}

	private String buildSignupRequestEmailContent(Long memberId, String name, String email, LocalDate birthday) {
		Context context = new Context();
		context.setVariable("memberId", memberId);
		context.setVariable("name", name);
		context.setVariable("email", email);
		context.setVariable("birthday", birthday);
		context.setVariable("baseUrl", baseUrl);

		return templateEngine.process("email/signup-request", context);
	}
}
