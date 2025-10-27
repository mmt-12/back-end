package com.memento.server.domain.achievement;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.memento.server.api.service.achievement.dto.response.SearchAchievementResponse;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {

	@Query("""
    SELECT new com.memento.server.api.service.achievement.dto.response.SearchAchievementResponse$Achievement(
        a.id,
        a.name,
        a.criteria,
        a.type,
        CASE WHEN aa.id IS NOT NULL THEN true ELSE false END
    )
    FROM Achievement a
    LEFT JOIN AchievementAssociate aa 
        ON a.id = aa.achievement.id 
        AND aa.associate.id = :associateId 
        AND aa.deletedAt IS NULL
""")
	List<SearchAchievementResponse.Achievement> findAllWithObtainedRecord(@Param("associateId") Long associateId);
	Optional<Achievement> findByIdAndDeletedAtNull(Long achievementId);

	int countByType(AchievementType achievementType);
}
