package com.memento.server.domain.voice;

import static com.memento.server.utility.validation.voice.VoiceValidator.*;
import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.lang.Boolean.*;
import static lombok.AccessLevel.PROTECTED;

import java.util.Objects;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.memento.server.common.BaseEntity;
import com.memento.server.domain.community.Associate;

import jakarta.persistence.Column;
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
@Table(name = "voices")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class Voice extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "name", length = 102, nullable = true)
	private String name;

	@Column(name = "url", length = 255, nullable = false)
	private String url;

	@Column(name = "temporary", nullable = false)
	private Boolean temporary;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "associate_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Associate associate;

	public static Voice createTemporary(String url, Associate associate) {
		validateUrl(url);
		validateAssociate(associate);

		return Voice.builder()
			.url(url)
			.temporary(true)
			.associate(associate)
			.build();
	}

	public static Voice createPermanent(String name, String url, Associate associate) {
		validateName(name);
		validateUrl(url);
		validateAssociate(associate);

		return Voice.builder()
			.name(name)
			.url(url)
			.temporary(false)
			.associate(associate)
			.build();
	}

	public boolean isTemporary() {
		return temporary;
	}

	public boolean isPermanent() {
		return FALSE.equals(temporary);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Voice voice)) {
			return false;
		}
		return getId() != null && Objects.equals(getId(), voice.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}
}
