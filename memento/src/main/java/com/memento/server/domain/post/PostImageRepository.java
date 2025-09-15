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
		      AND (pi.deletedAt IS NULL)
		      AND (p.deletedAt IS NULL)
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

	@Query("""
		    SELECT pi
		    FROM PostImage pi
		    JOIN pi.post p
		    WHERE p.memory.id = :memoryId
		      AND (pi.deletedAt IS NULL)
		      AND (p.deletedAt IS NULL)
		    ORDER BY p.memory.id, pi.createdAt DESC
		""")
	List<PostImage> findAllByMemoryId(Long memoryId);

	List<PostImage> findByPostIdAndDeletedAtNull(Long postId);

	List<PostImage> findAllByPostIdInAndDeletedAtNull(List<Long> postIds);

	List<PostImage> findAllByHashInAndDeletedAtIsNull(List<Hash> hashes);

	@Query("""
        SELECT COUNT(pi)
        FROM PostImage pi
        WHERE pi.post.associate.id = :associateId
        AND pi.deletedAt IS NULL
    """)
	int countByAssociateId(@Param("associateId") Long associateId);
}
