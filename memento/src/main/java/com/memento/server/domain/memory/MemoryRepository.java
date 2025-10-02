package com.memento.server.domain.memory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.memento.server.api.service.memory.dto.MemoryItem;

public interface MemoryRepository extends JpaRepository<Memory, Long> {

	@Query("""
		SELECT new com.memento.server.api.service.memory.dto.MemoryItem(
		    m.id,
		    e.title,
		    e.description,
		    e.period.startTime,
		    e.period.endTime,
		    e.location.latitude,
		    e.location.longitude,
		    e.location.code,
		    e.location.name,
		    e.location.address,
		    a.id,
		    a.nickname,
		    a.profileImageUrl,
		    ach.id,
		    ach.name
		)
		FROM Memory m
		JOIN m.event e
		JOIN e.associate a
		LEFT JOIN a.achievement ach
		WHERE e.community.id = :communityId
			AND (:keyword IS NULL OR e.title LIKE CONCAT('%', :keyword, '%'))
			AND (:startDate IS NULL OR e.period.startTime >= :startDate)
			AND (:endDate IS NULL OR e.period.endTime <= :endDate)
			AND (:cursor IS NULL OR m.id < :cursor)
			AND m.deletedAt IS NULL
		ORDER BY m.id DESC
		""")
	List<MemoryItem> findAllByConditions(
		@Param("communityId") Long communityId,
		@Param("keyword") String keyword,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate,
		@Param("cursor") Long cursor,
		Pageable pageable
	);

	Optional<Memory> findByIdAndDeletedAtIsNull(Long id);

	@Query("""
		SELECT m
		FROM Memory m
		JOIN FETCH m.event e
		WHERE m.id = :id AND m.deletedAt IS NULL
		""")
	Optional<Memory> findByIdWithEventAndDeletedAtIsNull(@Param("id") Long id);
}
