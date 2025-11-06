package com.memento.server.domain.memory;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.memento.server.api.controller.memory.dto.request.CreateUpdateMemoryRequest;
import com.memento.server.api.controller.memory.dto.request.CreateUpdateMemoryRequest.LocationRequest;
import com.memento.server.api.controller.memory.dto.request.CreateUpdateMemoryRequest.PeriodRequest;
import com.memento.server.common.BaseEntity;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.Community;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "memories")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class Memory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "title", length = 102, nullable = false)
	private String title;

	@Column(name = "description", length = 510, nullable = false)
	private String description;

	@Embedded
	private Location location;

	@Embedded
	private Period period;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "community_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Community community;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "associate_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Associate associate;

	public void update(CreateUpdateMemoryRequest request) {
		this.title = request.title();
		this.description = request.description();
		updatePeriod(request.period());
		updateLocation(request.location());
	}

	private void updateLocation(LocationRequest location) {
		this.location = Location.builder()
			.latitude(BigDecimal.valueOf(location.latitude()))
			.longitude(BigDecimal.valueOf(location.longitude()))
			.code(location.code())
			.name(location.name())
			.address(location.address())
			.build();
	}

	private void updatePeriod(PeriodRequest period) {
		this.period = Period.builder()
			.startTime(period.startTime())
			.endTime(period.endTime())
			.build();
	}
}
