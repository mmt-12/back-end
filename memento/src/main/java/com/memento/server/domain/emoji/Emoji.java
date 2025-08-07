package com.memento.server.domain.emoji;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

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
@Table(name = "emoji")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class Emoji extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "name", length = 102, nullable = false)
	private String name;

	@Column(name = "url", length = 255, nullable = false)
	private String url;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "associate_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Associate associate;

	public static Emoji create(String name, String url, Associate associate) {
		return Emoji.builder()
			.name(name)
			.url(url)
			.associate(associate)
			.build();
	}
}
