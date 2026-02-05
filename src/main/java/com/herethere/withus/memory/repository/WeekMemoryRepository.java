package com.herethere.withus.memory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.memory.domain.WeekMemory;

public interface WeekMemoryRepository extends JpaRepository<WeekMemory, Long> {
}
