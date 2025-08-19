package com.memento.server.domain.emoji;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmojiRepository extends JpaRepository<Emoji, Long> {
	Emoji findByIdAndDeletedAtNull(Long contentId);
}
