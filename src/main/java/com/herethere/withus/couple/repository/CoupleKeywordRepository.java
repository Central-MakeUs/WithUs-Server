package com.herethere.withus.couple.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.domain.CoupleKeyword;

public interface CoupleKeywordRepository extends JpaRepository<CoupleKeyword, Long> {
	List<CoupleKeyword> findAllByCouple(Couple couple);
}
