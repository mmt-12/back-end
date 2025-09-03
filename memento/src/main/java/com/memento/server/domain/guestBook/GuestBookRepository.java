package com.memento.server.domain.guestBook;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuestBookRepository extends JpaRepository<GuestBook, Long> {
	Optional<GuestBook> findByIdAndDeletedAtNull(Long guestBookId);

	@Query("""
	SELECT g FROM GuestBook g
	WHERE g.associate.id = :associateId
	  AND (:cursor IS NULL OR g.id <= :cursor)
	  AND g.deletedAt IS NULL
	ORDER BY g.id DESC
""")
	List<GuestBook> findPageByAssociateId(
		@Param("associateId") Long associateId,
		@Param("cursor") Long cursor,
		Pageable pageable
	);
}
