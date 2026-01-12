package com.herethere.withus.couple.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.herethere.withus.couple.domain.Couple;

import jakarta.persistence.LockModeType;

public interface CoupleRepository extends JpaRepository<Couple, Long> {
	@Query("""
		select c
		from Couple c
		where c.questionTime <= :nowTime
		  and (c.lastQuestionDate is null or c.lastQuestionDate < :today)
		""")
	List<Couple> findCouplesToProcess(
		@Param("nowTime") LocalTime nowTime,
		@Param("today") LocalDate today
	);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from Couple c where c.id = :id")
	Optional<Couple> findByIdWithLock(@Param("id") Long id);
}
