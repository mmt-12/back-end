package com.memento.server.domain.post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.memento.server.domain.memory.Memory;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

	@Query("""
		    SELECT pi
		    FROM PostImage pi
		    JOIN pi.post p
		    WHERE p.memory.id IN :memoryIds
		    ORDER BY p.memory.id, pi.createdAt DESC
		""")
	List<PostImage> findAllByMemoryIds(@Param("memoryIds") List<Long> memoryIds);

	@Query("""
		SELECT pi
		FROM PostImage pi
		JOIN pi.post p
		WHERE p.memory = :memory
		ORDER BY pi.id DESC
		""")
	List<PostImage> findAllByMemory(@Param("memory") Memory memory);
}
