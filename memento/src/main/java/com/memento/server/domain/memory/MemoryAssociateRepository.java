package com.memento.server.domain.memory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.memento.server.domain.memory.dto.MemoryAssociateCount;

public interface MemoryAssociateRepository extends JpaRepository<MemoryAssociate, Long> {

	@Query("""
		    SELECT ma.memory.id AS memoryId, COUNT(ma) AS associateCount
		    FROM MemoryAssociate ma
		    WHERE ma.memory.id IN :memoryIds
		      AND (ma.deletedAt IS NULL)
		    GROUP BY ma.memory.id
		""")
	List<MemoryAssociateCount> countAssociatesByMemoryIds(@Param("memoryIds") List<Long> memoryIds);

	@Query("""
		    SELECT COUNT(ma)
		    FROM MemoryAssociate ma
		    WHERE ma.memory.id = :memoryId
		      AND (ma.deletedAt IS NULL)
		""")
	Long countAssociatesByMemoryId(@Param("memoryId") Long memoryId);

	List<MemoryAssociate> findAllByMemoryAndDeletedAtIsNull(Memory memory);
}
