package com.memento.server.domain.event;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class Period {

	@Column(name = "start_time", nullable = true)
	private LocalDateTime startTime;

	@Column(name = "end_time", nullable = true)
	private LocalDateTime endTime;
}
