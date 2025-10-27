package com.memento.server.domain.comment;

import static com.memento.server.domain.comment.CommentType.*;
import static com.memento.server.domain.comment.CommentValidator.validateAssociate;
import static com.memento.server.domain.comment.CommentValidator.validatePost;
import static com.memento.server.domain.comment.CommentValidator.validateUrl;
import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.util.Objects;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.memento.server.common.BaseEntity;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.post.Post;

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
@Table(name = "comments")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "url", length = 255, nullable = false)
	private String url;

	@Enumerated(STRING)
	private CommentType type;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Post post;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "associate_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Associate associate;

	public static Comment createVoiceComment(String url, Post post, Associate associate) {
		validateUrl(url);
		validatePost(post);
		validateAssociate(associate);

		return Comment.builder()
			.url(url)
			.type(VOICE)
			.post(post)
			.associate(associate)
			.build();
	}

	public static Comment createEmojiComment(String url, Post post, Associate associate) {
		validateUrl(url);
		validatePost(post);
		validateAssociate(associate);

		return Comment.builder()
			.url(url)
			.type(EMOJI)
			.post(post)
			.associate(associate)
			.build();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Comment comment)) {
			return false;
		}
		return id != null && Objects.equals(id, comment.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
