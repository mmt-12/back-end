package com.memento.server.domain.voice;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceRepository extends JpaRepository<Voice, Long>, VoiceRepositoryCustom {
	Optional<Voice> findByIdAndDeletedAtIsNull(Long id);
}
