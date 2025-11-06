package com.memento.server.domain.guestBook;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.memento.server.common.BaseEntity;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.emoji.Emoji;
import com.memento.server.domain.voice.Voice;

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
@Table(name = "guest_books")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class GuestBook extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Enumerated(STRING)
	@Column(name = "type", nullable = false)
	private GuestBookType type;

	@Column(name = "content", length = 510, nullable = false)
	private String content;

	@Column(name = "name", length = 102)
	private String name;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "associate_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Associate associate;

	public static GuestBook createText(Associate associate, String content, GuestBookType type){
		return GuestBook.builder()
			.associate(associate)
			.content(content)
			.type(type)
			.build();
	}

	public static GuestBook createVoice(Associate associate, Voice voice, GuestBookType type){
		return GuestBook.builder()
			.associate(associate)
			.content(voice.getUrl())
			.name(voice.getName())
			.type(type)
			.build();
	}

	public static GuestBook createEmoji(Associate associate, Emoji emoji, GuestBookType type){
		return GuestBook.builder()
			.associate(associate)
			.content(emoji.getUrl())
			.name(emoji.getName())
			.type(type)
			.build();
	}

	public static GuestBook createTemporary(Associate associate, String url){
		return GuestBook.builder()
			.associate(associate)
			.content(url)
			.type(GuestBookType.VOICE)
			.build();
	}
}
