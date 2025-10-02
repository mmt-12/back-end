package com.memento.server.domain.achievement;

import static com.memento.server.utility.validation.achievement.AchievementValidator.*;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.util.Objects;

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

	public static Achievement create(String name, String criteria, AchievementType type) {
		validateName(name);
		validateCriteria(criteria);
		validateType(type);

		return Achievement.builder()
			.name(name)
			.criteria(criteria)
			.type(type)
			.build();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Achievement achievement)) {
			return false;
		}
		return getId() != null && Objects.equals(getId(), achievement.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}
}
