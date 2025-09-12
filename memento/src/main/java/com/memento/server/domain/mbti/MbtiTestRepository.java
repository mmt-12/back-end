package com.memento.server.domain.mbti;

import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.memento.server.api.service.mbti.dto.MbtiSearchDto;

public interface MbtiTestRepository extends JpaRepository<MbtiTest, Long> {
	MbtiTest findByFromAssociateIdAndToAssociateId(Long from, Long to);

	@Query("""
    SELECT new com.memento.server.api.service.mbti.dto.MbtiSearchDto(
        SUM(CASE WHEN m.mbti = 'INFP' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'INFJ' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'INTP' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'INTJ' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'ISFP' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'ISFJ' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'ISTP' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'ISTJ' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'ENFP' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'ENFJ' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'ENTP' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'ENTJ' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'ESFP' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'ESFJ' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'ESTP' THEN 1 ELSE 0 END),
        SUM(CASE WHEN m.mbti = 'ESTJ' THEN 1 ELSE 0 END)
    )
    FROM MbtiTest m
    WHERE m.toAssociate.id = :associateId
""")
	MbtiSearchDto countMbtiByToAssociate(@Param("associateId") Long associateId);

	@Query(value = """
    SELECT
    COUNT(DISTINCT mbti) AS total_count,
    COALESCE(SUM(mbti IN ('ISFP','ISFJ','INFP','INFJ','ESFP','ESFJ','ENFP','ENFJ')), 0) AS f_count,
    COALESCE(SUM(mbti IN ('ISTP','ISTJ','INTP','INTJ','ESTP','ESTJ','ENTP','ENTJ')), 0) AS t_count
    FROM mbti_test
    WHERE to_associate_id = :toAssociateId
""", nativeQuery = true)
	Map<String, Object> countAllByToAssociate(@Param("toAssociateId") Long toAssociateId);

	int countByFromAssociateId(Long fromAssociateId);
}
