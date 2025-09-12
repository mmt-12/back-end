package com.memento.server.domain.community;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssociateRepository extends JpaRepository<Associate, Long> {
	Optional<Associate> findByIdAndDeletedAtNull(Long associateId);

	@Query("""
		SELECT a
		FROM Associate a
		WHERE a.community.id = :communityId
		  AND (:keyword IS NULL OR a.nickname LIKE CONCAT('%', :keyword, '%'))
		  AND (a.deletedAt IS NULL)
		ORDER BY a.id DESC
		""")
	List<Associate> findAllByCommunityIdAndKeyword(
		@Param("communityId") Long communityId,
		@Param("keyword") String keyword
	);

	List<Associate> findAllByMemberIdAndDeletedAtIsNull(Long memberId);

	Optional<Associate> findByIdAndDeletedAtIsNull(Long id);

	List<Associate> findAllByIdInAndDeletedAtIsNull(List<Long> ids);

	List<Associate> findAllByCommunityId(Long communityId);

	@Query("SELECT ma.associate FROM MemoryAssociate ma WHERE ma.memory.id = :memoryId")
	List<Associate> findAllByMemoryId(@Param("memoryId") Long memoryId);

	@Query("SELECT p.associate FROM Post p WHERE p.id = :postId")
	Optional<Associate> findByPostId(Long postId);

	Optional<Associate> findByMemberIdAndDeletedAtIsNull(Long memberId);
}
