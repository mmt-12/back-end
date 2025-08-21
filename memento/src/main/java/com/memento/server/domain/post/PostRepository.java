package com.memento.server.domain.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
	Optional<Post> findByIdAndDeletedAtIsNull(Long postId);

	@Query("""
    SELECT p
    FROM Post p
    WHERE p.memory.id = :memoryId
      AND (:cursor IS NULL OR p.id < :cursor)
      AND p.deletedAt IS NULL
    ORDER BY p.id DESC
    """)
	List<Post> findAllByMemoryIdAndCursor(
		@Param("memoryId") Long memoryId,
		@Param("cursor") Long cursor,
		Pageable pageable
	);
}
