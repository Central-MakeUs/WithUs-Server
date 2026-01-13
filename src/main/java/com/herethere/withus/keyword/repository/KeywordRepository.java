package com.herethere.withus.keyword.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.keyword.domain.Keyword;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}
