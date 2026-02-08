package com.herethere.withus.couple.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.user.domain.User;

import jakarta.persistence.LockModeType;

public interface CoupleRepository extends JpaRepository<Couple, Long> {
	@Query("""
		SELECT c
		FROM Couple c
		WHERE c.lastQuestionDate < :today
		AND c.userADeletedAt IS NULL
		AND c.userBDeletedAt IS NULL
		AND (
		    NOT EXISTS (
		        SELECT 1
		        FROM CoupleQuestion cq
		        WHERE cq.couple = c
		    )
		    OR
		    EXISTS (
		        SELECT 1
		        FROM CoupleQuestion cq
		        JOIN QuestionPicture qp ON qp.coupleQuestion = cq
		        WHERE cq.couple = c
		          AND cq.date = (
		              SELECT MAX(cq2.date)
		              FROM CoupleQuestion cq2
		              WHERE cq2.couple = c
		          )
		    )
		)
		""")
	List<Couple> findCouplesToProcess(@Param("today") LocalDate today);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from Couple c where c.id = :id")
	Optional<Couple> findByIdWithLock(@Param("id") Long id);

	@Query("""
		SELECT c FROM Couple c
		WHERE (c.userA = :user AND c.userADeletedAt IS NULL)
		OR (c.userB = :user AND c.userBDeletedAt IS NULL)
		""")
	Optional<Couple> findActiveCouple(@Param("user") User user);

	@Query("""
		SELECT COUNT(c) > 0 FROM Couple c
		WHERE (c.userA = :user AND c.userADeletedAt IS NULL)
		OR (c.userB = :user AND c.userBDeletedAt IS NULL)""")
	boolean existsActiveCouple(@Param("user") User user);
}
