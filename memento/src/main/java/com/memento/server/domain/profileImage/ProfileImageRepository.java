package com.memento.server.domain.profileImage;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
	@Query("""
	SELECT p FROM ProfileImage p
	WHERE p.associate.id = :associateId
	  AND (:cursor IS NULL OR p.id < :cursor)
	  AND p.deletedAt IS NULL
	ORDER BY p.id DESC
""")
	List<ProfileImage> findProfileImageByAssociateId(
		@Param("associateId") Long associateId,
		@Param("cursor") Long cursor,
		Pageable pageable
	);

	Optional<ProfileImage> findByIdAndDeletedAtIsNull(Long profileImageId);

	List<ProfileImage> findAllByAssociateId(Long associateId);
}
