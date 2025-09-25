package com.memento.server.domain.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
	int countByAssociateIdAndDeletedAtNull(Long associateId);
}
