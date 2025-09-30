package com.memento.server.domain.fcm;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {
	void deleteByToken(String token);
	@Query("SELECT ft FROM FCMToken ft WHERE ft.associate.id IN :associateIds")
	List<FCMToken> findAllByAssociateIds(@Param("associateIds") List<Long> associateIds);
}
