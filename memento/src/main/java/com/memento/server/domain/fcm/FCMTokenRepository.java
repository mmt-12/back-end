package com.memento.server.domain.fcm;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {
	List<FCMToken> findByAssociateId(Long associateId);
	void deleteByToken(String token);
	boolean existsByToken(String token);
}
