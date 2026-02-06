package com.herethere.withus.memory.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.memory.domain.WeekMemory;

public interface WeekMemoryRepository extends JpaRepository<WeekMemory, Long> {
	List<WeekMemory> findAllByCoupleAndMonthKey(Couple couple, int monthKey);

	boolean existsByCoupleAndWeekEndDate(Couple couple, LocalDate weekEndDate);
}
