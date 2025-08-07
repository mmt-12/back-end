package com.memento.server.spring.domain.emoji;

import static com.memento.server.common.error.ErrorCodes.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.emoji.Emoji;

public class EmojiTest {

	private static final String VALID_NAME = "emoji";
	private static final String VALID_URL = "https://example.com/image.png";
	private static final Associate VALID_ASSOCIATE = AssociateFixtures.associate();

	@Test
	@DisplayName("이모지를 생성한다.")
	void create() {
	    // when
		Emoji emoji = Emoji.create(VALID_NAME, VALID_URL, VALID_ASSOCIATE);

	    // then
		assertThat(emoji).isNotNull();
		assertThat(emoji.getName()).isEqualTo(VALID_NAME);
		assertThat(emoji.getUrl()).isEqualTo(VALID_URL);
		assertThat(emoji.getAssociate()).isEqualTo(VALID_ASSOCIATE);
	}

	@Test
	@DisplayName("이모지 생성 시 이름이 null이면 EMOJI_NAME_REQUIRED 예외가 발생한다.")
	void createEmoji_withNullName_throwsException() {
		assertThatThrownBy(() -> Emoji.create(null, VALID_URL, VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", EMOJI_NAME_REQUIRED);
	}

	@Test
	@DisplayName("이모지 생성 시 이름이 공백이면 EMOJI_NAME_BLANK 예외가 발생한다.")
	void createEmoji_withBlankName_throwsException() {
		assertThatThrownBy(() -> Emoji.create("   ", VALID_URL, VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", EMOJI_NAME_BLANK);
	}

	@Test
	@DisplayName("이모지 생성 시 이름이 102자를 초과하면 EMOJI_NAME_TOO_LONG 예외가 발생한다.")
	void createEmoji_withTooLongName_throwsException() {
		// given
		String tooLongName = "a".repeat(103);

		// when && then
		assertThatThrownBy(() -> Emoji.create(tooLongName, VALID_URL, VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", EMOJI_NAME_TOO_LONG);
	}

	@Test
	@DisplayName("이모지 생성 시 URL이 null이면 EMOJI_URL_REQUIRED 예외가 발생한다.")
	void createEmoji_withNullUrl_throwsException() {
		assertThatThrownBy(() -> Emoji.create(VALID_NAME, null, VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", EMOJI_URL_REQUIRED);
	}

	@Test
	@DisplayName("이모지 생성 시 URL이 공백이면 EMOJI_URL_BLANK 예외가 발생한다.")
	void createEmoji_withBlankUrl_throwsException() {
		assertThatThrownBy(() -> Emoji.create(VALID_NAME, "   ", VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", EMOJI_URL_BLANK);
	}

	@Test
	@DisplayName("이모지 생성 시 url이 255자를 초과하면 예외를 던진다.")
	void createEmoji_withTooLongUrl_throwsException() {
		// given
		String tooLongUrl = "a".repeat(256);

		// when && then
		assertThatThrownBy(() -> Emoji.create(VALID_NAME, tooLongUrl, VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", EMOJI_URL_TOO_LONG);
	}

	@Test
	@DisplayName("이모지 생성 시 associate가 null이면 EMOJI_ASSOCIATE_REQUIRED 예외가 발생한다.")
	void createEmoji_withNullAssociate_throwsException() {
		assertThatThrownBy(() -> Emoji.create(VALID_NAME, VALID_URL, null))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", EMOJI_ASSOCIATE_REQUIRED);
	}
}
