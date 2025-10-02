package com.memento.server.domain.achievement;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementAssociateRepository extends JpaRepository<AchievementAssociate, Long> {
	boolean existsByAchievementIdAndAssociateId(long l, Long associateId);

	int countByAssociateIdAndAchievementTypeAndDeletedAtNull(Long associateId, AchievementType type);
}
