package com.memento.server.domain.community;

import java.util.List;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

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
		  AND (:cursor IS NULL OR a.id < :cursor)
		  AND (a.deletedAt IS NULL)
		ORDER BY a.id DESC
		""")
	List<Associate> findAllByCommunityIdAndKeywordWithCursor(
		@Param("communityId") Long communityId,
		@Param("keyword") String keyword,
		@Param("cursor") Long cursor,
		Pageable pageable
	);

	List<Associate> findAllByMemberIdAndDeletedAtIsNull(Long memberId);

	Optional<Associate> findByIdAndDeletedAtIsNull(Long id);

	List<Associate> findAllByIdInAndDeletedAtIsNull(List<Long> ids);
}
