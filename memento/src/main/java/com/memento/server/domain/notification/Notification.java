package com.memento.server.domain.notification;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.memento.server.common.BaseEntity;
import com.memento.server.domain.community.Associate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
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
@Table(name = "notifications")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "title", length = 102, nullable = false)
	private String title;

	@Column(name = "content", length = 255, nullable = false)
	private String content;

	@Column(name = "is_read", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private Boolean isRead;

	@Column(name = "end_point_id", nullable = true)
	private Long endPointId;

	@Enumerated(STRING)
	@Column(name = "type")
	private NotificationType type;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "associate_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Associate associate;
}
