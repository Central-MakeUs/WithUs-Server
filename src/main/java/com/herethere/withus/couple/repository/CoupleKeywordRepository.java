package com.herethere.withus.couple.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.domain.CoupleKeyword;
import com.herethere.withus.couple.domain.CoupleKeywordStatus;
import com.herethere.withus.keyword.domain.Keyword;

public interface CoupleKeywordRepository extends JpaRepository<CoupleKeyword, Long> {
	List<CoupleKeyword> findAllByCouple(Couple couple);

	List<CoupleKeyword> findAllByCoupleAndStatus(Couple couple, CoupleKeywordStatus status);

	Optional<CoupleKeyword> findByCoupleAndKeyword(Couple couple, Keyword keyword);
}
