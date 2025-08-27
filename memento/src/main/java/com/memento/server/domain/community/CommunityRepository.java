package com.memento.server.domain.community;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {

	Optional<Community> findByIdAndDeletedAtIsNull(Long id);
}
