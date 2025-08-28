package com.memento.server.domain.emoji;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmojiRepository extends JpaRepository<Emoji, Long>, EmojiRepositoryCustom {
	Optional<Emoji> findByIdAndDeletedAtIsNull(Long id);

	Emoji findByIdAndDeletedAtNull(Long contentId);

	boolean existsByNameAndDeletedAtIsNull(String name);
}
