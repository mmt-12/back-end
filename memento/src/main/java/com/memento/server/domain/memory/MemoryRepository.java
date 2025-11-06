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

	int countByAssociateIdAndDeletedAtNull(Long associateId);

	@Query("""
		SELECT new com.memento.server.api.service.memory.dto.MemoryItem(
		    m.id,
		    m.title,
		    m.description,
		    m.period.startTime,
		    m.period.endTime,
		    m.location.latitude,
		    m.location.longitude,
		    m.location.code,
		    m.location.name,
		    m.location.address,
		    a.id,
		    a.nickname,
		    a.profileImageUrl,
		    ach.id,
		    ach.name
		)
		FROM Memory m
		JOIN m.associate a
		LEFT JOIN a.achievement ach
		WHERE m.community.id = :communityId
			AND (:keyword IS NULL OR m.title LIKE CONCAT('%', :keyword, '%'))
			AND (:startDate IS NULL OR m.period.startTime >= :startDate)
			AND (:endDate IS NULL OR m.period.endTime <= :endDate)
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
}
