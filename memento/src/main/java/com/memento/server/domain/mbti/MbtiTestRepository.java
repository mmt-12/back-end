package com.memento.server.domain.mbti;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MbtiTestRepository extends JpaRepository<MbtiTest, Long> {
	MbtiTest findByFromAssociateIdAndToAssociateId(Long id, Long id1);

	@Query(value = """
    SELECT 
        SUM(CASE WHEN m.mbti = 'INFP' THEN 1 ELSE 0 END) AS INFP,
        SUM(CASE WHEN m.mbti = 'INFJ' THEN 1 ELSE 0 END) AS INFJ,
        SUM(CASE WHEN m.mbti = 'INTP' THEN 1 ELSE 0 END) AS INTP,
        SUM(CASE WHEN m.mbti = 'INTJ' THEN 1 ELSE 0 END) AS INTJ,
        SUM(CASE WHEN m.mbti = 'ISFP' THEN 1 ELSE 0 END) AS ISFP,
        SUM(CASE WHEN m.mbti = 'ISFJ' THEN 1 ELSE 0 END) AS ISFJ,
        SUM(CASE WHEN m.mbti = 'ISTP' THEN 1 ELSE 0 END) AS ISTP,
        SUM(CASE WHEN m.mbti = 'ISTJ' THEN 1 ELSE 0 END) AS ISTJ,
        SUM(CASE WHEN m.mbti = 'ENFP' THEN 1 ELSE 0 END) AS ENFP,
        SUM(CASE WHEN m.mbti = 'ENFJ' THEN 1 ELSE 0 END) AS ENFJ,
        SUM(CASE WHEN m.mbti = 'ENTP' THEN 1 ELSE 0 END) AS ENTP,
        SUM(CASE WHEN m.mbti = 'ENTJ' THEN 1 ELSE 0 END) AS ENTJ,
        SUM(CASE WHEN m.mbti = 'ESFP' THEN 1 ELSE 0 END) AS ESFP,
        SUM(CASE WHEN m.mbti = 'ESFJ' THEN 1 ELSE 0 END) AS ESFJ,
        SUM(CASE WHEN m.mbti = 'ESTP' THEN 1 ELSE 0 END) AS ESTP,
        SUM(CASE WHEN m.mbti = 'ESTJ' THEN 1 ELSE 0 END) AS ESTJ
    FROM mbti_test m
    WHERE m.to_associate_id = :associateId
""", nativeQuery = true)
	Object[] countMbtiByToAssociate(@Param("associateId") Long associateId);
}
