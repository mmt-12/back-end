package com.memento.server.domain.achievement;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.memento.server.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "achievements")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class Achievement extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "name", length = 102, nullable = false)
	private String name;

	@Column(name = "criteria", length = 255, nullable = false)
	private String criteria;

	@Enumerated(STRING)
	@Column(name = "type", nullable = false)
	private AchievementType type;
}
