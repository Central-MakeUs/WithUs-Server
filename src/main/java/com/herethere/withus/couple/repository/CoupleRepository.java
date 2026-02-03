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
		select c
		from Couple c
		where (c.lastQuestionDate < :today)
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
