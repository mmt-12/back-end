package com.memento.server.domain.event;

import static lombok.AccessLevel.PROTECTED;

import java.math.BigDecimal;

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
public class Location {

	@Column(name = "latitude", nullable = true, columnDefinition = "DECIMAL(10, 7)")
	private BigDecimal latitude;

	@Column(name = "longitude", nullable = true, columnDefinition = "DECIMAL(10, 7)")
	private BigDecimal longitude;

	@Column(name = "code", nullable = true)
	private Integer code;

	@Column(name = "name", length = 102, nullable = true)
	private String name;

	@Column(name = "address", length = 255, nullable = true)
	private String address;
}