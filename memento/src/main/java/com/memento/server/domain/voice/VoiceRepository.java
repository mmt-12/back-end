package com.memento.server.domain.voice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceRepository extends JpaRepository<Voice, Long>, VoiceRepositoryCustom {
}
