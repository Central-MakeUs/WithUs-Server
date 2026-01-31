package com.herethere.withus.archive.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.herethere.withus.archive.dto.internal.ArchiveDayView;
import com.herethere.withus.archive.dto.internal.ArchiveDetailView;
import com.herethere.withus.question.domain.QuestionPicture;

public interface ArchiveRepository extends JpaRepository<QuestionPicture, Long> {

	@Query(value = """
		SELECT DISTINCT all_dates.date
		FROM (
		    -- 1. 사진이 있는 질문 날짜
		    SELECT cq.date
		    FROM couple_question cq
		    JOIN question_picture qp ON qp.couple_question_id = cq.id
		    WHERE cq.couple_id = :coupleId
		
		    UNION
		
		    -- 2. 키워드 기록이 있는 날짜
		    SELECT kr.date
		    FROM keyword_record kr
		    JOIN couple_keyword ck ON kr.couple_keyword_id = ck.id
		    WHERE ck.couple_id = :coupleId
		) AS all_dates
		WHERE all_dates.date < :today 
		AND (:lastDate IS NULL OR all_dates.date < :lastDate)
		ORDER BY all_dates.date DESC
		LIMIT :size
		""", nativeQuery = true)
	List<LocalDate> findTargetDates(
		@Param("coupleId") Long coupleId,
		@Param("lastDate") LocalDate lastDate,
		@Param("today") LocalDate today,
		@Param("size") int size
	);

	@Query(value = """
		SELECT * FROM (
		    -- 1. 질문 사진 상세 (sortOrder 1, 키워드가 아니므로 content는 NULL)
		    SELECT 
		        cq.date AS date,
		        'QUESTION' AS archiveType,
		        cq.id AS sourceId,
		        NULL AS content, -- 정렬을 위한 컬럼 맞춤
		        MAX(CASE WHEN qp.user_id = :meId THEN qp.image_key END) AS meImageKey,
		        MAX(CASE WHEN qp.user_id = :partnerId THEN qp.image_key END) AS partnerImageKey,
		        1 AS sortOrder
		    FROM couple_question cq
		    JOIN question_picture qp ON qp.couple_question_id = cq.id
		    WHERE cq.couple_id = :coupleId AND cq.date IN (:targetDates)
		    GROUP BY cq.date, cq.id
		
		    UNION ALL
		
		    -- 2. 키워드 기록 상세 (sortOrder 2)
		    SELECT 
		        kr.date AS date,
		        'KEYWORD' AS archiveType,
		        ck.id AS sourceId,
		        k.content AS content, -- 키워드 텍스트 추출
		        MAX(CASE WHEN kr.user_id = :meId THEN kr.image_key END) AS meImageKey,
		        MAX(CASE WHEN kr.user_id = :partnerId THEN kr.image_key END) AS partnerImageKey,
		        2 AS sortOrder
		    FROM keyword_record kr
		    JOIN couple_keyword ck ON kr.couple_keyword_id = ck.id
		    JOIN keyword k ON ck.keyword_id = k.id -- 키워드 텍스트 조인
		    WHERE ck.couple_id = :coupleId AND kr.date IN (:targetDates)
		    GROUP BY kr.date, ck.id, k.content
		) AS combined
		-- 정렬: 1순위 날짜(내림차순), 2순위 타입(질문 우선), 3순위 키워드 내용(오름차순)
		ORDER BY date DESC, sortOrder ASC, content ASC
		""", nativeQuery = true)
	List<ArchiveDayView> findAllByDates(
		@Param("coupleId") Long coupleId,
		@Param("meId") Long meId,
		@Param("partnerId") Long partnerId,
		@Param("targetDates") List<LocalDate> targetDates
	);

	@Query(value = """
		SELECT * FROM (
		      -- 1. 질문 섹션: 한 명이라도 올렸으면 행이 생성됨
		      SELECT
		          'QUESTION' AS archiveType,
		          cq.id AS sourceId,
		          q.content AS content,
		          MAX(CASE WHEN qp.user_id = :meId THEN qp.image_key END) AS meImageKey,
		          MAX(CASE WHEN qp.user_id = :meId THEN qp.created_at END) AS meAnsweredAt,
		          MAX(CASE WHEN qp.user_id = :partnerId THEN qp.image_key END) AS partnerImageKey,
		          MAX(CASE WHEN qp.user_id = :partnerId THEN qp.created_at END) AS partnerAnsweredAt,
		          1 AS sortOrder
		      FROM couple_question cq
		      JOIN question q ON cq.question_id = q.id
		      INNER JOIN question_picture qp ON qp.couple_question_id = cq.id
		      WHERE cq.couple_id = :coupleId AND cq.date = :date
		      GROUP BY cq.id, q.content
		    UNION ALL
		
		    -- 2. 키워드 섹션
		    SELECT 
		        'KEYWORD' AS archiveType,
		        ck.id AS sourceId,
		        k.content AS content,
		        MAX(CASE WHEN kr.user_id = :meId THEN kr.image_key END) AS meImageKey,
		        MAX(CASE WHEN kr.user_id = :meId THEN kr.created_at END) AS meAnsweredAt,
		        MAX(CASE WHEN kr.user_id = :partnerId THEN kr.image_key END) AS partnerImageKey,
		        MAX(CASE WHEN kr.user_id = :partnerId THEN kr.created_at END) AS partnerAnsweredAt,
		        2 AS sortOrder
		    FROM couple_keyword ck
		    INNER JOIN keyword k ON ck.keyword_id = k.id
		    INNER JOIN keyword_record kr ON kr.couple_keyword_id = ck.id AND kr.date = :date
		    WHERE ck.couple_id = :coupleId
		    GROUP BY ck.id, k.content
		) AS detail
		ORDER BY sortOrder ASC, content ASC
		""", nativeQuery = true)
	List<ArchiveDetailView> findDetailByDate(
		@Param("coupleId") Long coupleId,
		@Param("meId") Long meId,
		@Param("partnerId") Long partnerId,
		@Param("date") LocalDate date
	);
}
