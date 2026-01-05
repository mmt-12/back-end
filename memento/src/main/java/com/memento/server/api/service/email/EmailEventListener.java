package com.memento.server.api.service.email;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.memento.server.api.service.email.dto.event.SignupRequestEvent;

import lombok.RequiredArgsConstructor;

@Async("appExecutor")
@Component
@RequiredArgsConstructor
public class EmailEventListener {

	private final EmailService emailService;

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleSignupRequestEmail(SignupRequestEvent event) {
		emailService.sendSignupRequestEmail(
			event.memberId(),
			event.name(),
			event.email(),
			event.birthday()
		);
	}
}
