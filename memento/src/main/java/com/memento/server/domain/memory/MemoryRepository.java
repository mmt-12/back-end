package com.memento.server.domain.memory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemoryRepository extends JpaRepository<Memory, Long> {

	@Query("""
		SELECT m
		FROM Memory m
		WHERE m.event.community.id = :communityId
		  AND (:keyword IS NULL OR m.event.title LIKE CONCAT('%', :keyword, '%'))
		  AND (:startDate IS NULL OR m.event.period.startTime >= :startDate)
		  AND (:endDate IS NULL OR m.event.period.endTime <= :endDate)
		  AND (:cursor IS NULL OR m.id < :cursor)
		  AND (m.deletedAt IS NULL)
		ORDER BY m.id DESC
		""")
	List<Memory> findAllByConditions(
		@Param("communityId") Long communityId,
		@Param("keyword") String keyword,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate,
		@Param("cursor") Long cursor,
		Pageable pageable
	);

	Optional<Memory> findByIdAndDeletedAtIsNull(Long id);
}
