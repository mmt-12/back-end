package com.memento.server.domain.community;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssociateRepository extends JpaRepository<Associate, Long> {
	Optional<Associate> findByIdAndDeletedAtNull(Long associateId);
}
