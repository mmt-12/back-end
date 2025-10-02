package com.memento.server.domain.community;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssociateStatsRepository extends JpaRepository<AssociateStats, Long> {
	Optional<AssociateStats> findByAssociateId(Long associateId);

	List<AssociateStats> findByAssociateIdIn(List<Long> associateIds);
}
