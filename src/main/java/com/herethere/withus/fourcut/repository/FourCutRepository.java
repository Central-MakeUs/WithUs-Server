package com.herethere.withus.fourcut.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.herethere.withus.fourcut.domain.FourCut;

public interface FourCutRepository extends JpaRepository<FourCut, Long> {

	@Query("""
        SELECT f
        FROM FourCut f
        WHERE f.couple.id = :coupleId
          AND (
               :cursorCreatedAt IS NULL
               OR f.createdAt < :cursorCreatedAt
               OR (f.createdAt = :cursorCreatedAt AND f.id < :cursorId)
          )
        ORDER BY f.createdAt DESC, f.id DESC
    """)
	List<FourCut> findFourCutsByCursor(
		@Param("coupleId") Long coupleId,
		@Param("cursorCreatedAt") LocalDateTime cursorCreatedAt,
		@Param("cursorId") Long cursorId,
		Pageable pageable
	);
}
