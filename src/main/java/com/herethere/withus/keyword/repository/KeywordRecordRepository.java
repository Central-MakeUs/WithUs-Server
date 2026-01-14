package com.herethere.withus.keyword.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.keyword.domain.KeywordRecord;

public interface KeywordRecordRepository extends JpaRepository<KeywordRecord, Long> {
}
