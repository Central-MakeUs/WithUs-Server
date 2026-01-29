package com.herethere.withus.archive.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.herethere.withus.archive.dto.internal.ArchiveDayDto;
import com.herethere.withus.question.domain.QuestionPicture;

public interface ArchiveRepository extends JpaRepository<QuestionPicture, Long> {

	@Query(value = """
		WITH
		-- 1. question_picture가 존재하는 날짜들
		question_dates AS (
		    SELECT DISTINCT
		        cq.date,
		        cq.couple_id
		    FROM question_picture qp
		    JOIN couple_question cq ON qp.couple_question_id = cq.id
		    WHERE cq.couple_id = :coupleId
		),
		
		-- 2. 해당 날짜에 실제로 올라온 keyword_record 중에서 대표 1개 뽑기
		keyword_ranked AS (
		    SELECT
		        kr.id,
		        kr.user_id,
		        kr.image_key,
		        kr.date,
		        ck.couple_id,
		        k.content,
		        ROW_NUMBER() OVER (
		            PARTITION BY ck.couple_id, kr.date
		            ORDER BY k.content ASC
		        ) AS rn
		    FROM keyword_record kr
		    JOIN couple_keyword ck ON kr.couple_keyword_id = ck.id
		    JOIN keyword k ON ck.keyword_id = k.id
		    WHERE ck.couple_id = :coupleId
		),
		
		keyword_rep AS (
		    SELECT *
		    FROM keyword_ranked
		    WHERE rn = 1
		),
		
		-- 3. 응답에 포함될 날짜 집합
		representative_dates AS (
		    SELECT date FROM question_dates
		    UNION
		    SELECT date FROM keyword_rep
		),
		
		-- 4. 날짜별로 me / partner 이미지 채우기
		final_rows AS (
			SELECT
				rd.date AS date,
		
				:meId AS meUserId,
				COALESCE(
					MAX(CASE WHEN qp.user_id = :meId THEN qp.imageKey END),
					MAX(CASE WHEN kr.user_id = :meId THEN kr.image_key END)
				) AS meImageKey,
		
				:partnerId AS partnerUserId,
				COALESCE(
					MAX(CASE WHEN qp.user_id = :partnerId THEN qp.imageKey END),
					MAX(CASE WHEN kr.user_id = :partnerId THEN kr.image_key END)
				) AS partnerImageKey
		
			FROM representative_dates rd
		
			LEFT JOIN couple_question cq
				ON cq.date = rd.date AND cq.couple_id = :coupleId
			LEFT JOIN question_picture qp
				ON qp.couple_question_id = cq.id
		
			LEFT JOIN keyword_rep kr
				ON kr.date = rd.date AND kr.couple_id = :coupleId
		
			GROUP BY rd.date
		)
		
		SELECT *
		FROM final_rows
		WHERE (:lastDate IS NULL OR date < :lastDate)
		ORDER BY date DESC
		LIMIT :size;
		""", nativeQuery = true)
	List<ArchiveDayDto> findArchiveDaysByCursor(
		@Param("coupleId") Long coupleId,
		@Param("meId") Long meId,
		@Param("partnerId") Long partnerId,
		@Param("lastDate") LocalDate lastDate,
		@Param("size") int size
	);
}
