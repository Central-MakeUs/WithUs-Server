package com.herethere.withus.question.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.herethere.withus.question.domain.CoupleQuestion;
import com.herethere.withus.question.domain.QuestionPicture;
import com.herethere.withus.user.domain.User;

public interface QuestionPictureRepository extends JpaRepository<QuestionPicture, Long> {
	boolean existsByUserAndCoupleQuestion(User user, CoupleQuestion coupleQuestion);

	Optional<QuestionPicture> findByUserAndCoupleQuestion(User user, CoupleQuestion coupleQuestion);

	List<QuestionPicture> findByCoupleQuestion(CoupleQuestion coupleQuestion);

	@Query("""
		SELECT q FROM QuestionPicture q
		JOIN FETCH q.coupleQuestion qc
		WHERE qc.couple.id = :coupleId
		AND qc.date >= :start AND qc.date <= :end
		""")
	List<QuestionPicture> findAllByCoupleInPeriod(
		@Param("coupleId") Long coupleId,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);
}
