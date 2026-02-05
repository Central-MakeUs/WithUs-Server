package com.herethere.withus.memory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.memory.domain.CustomMemory;

public interface CustomMemoryRepository extends JpaRepository<CustomMemory, Long> {
	List<CustomMemory> findAllByCoupleAndMonthKey(Couple couple, int monthKey);
}
