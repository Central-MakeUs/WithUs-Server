package com.herethere.withus.memory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.memory.domain.CustomMemory;

public interface CustomMemoryRepository extends JpaRepository<CustomMemory, Long> {
}
