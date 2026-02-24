package com.herethere.withus.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.herethere.withus.notification.domain.NotificationHistory;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {

	@Query("""
		SELECT n FROM NotificationHistory n
		WHERE n.user.id = :userId
		ORDER BY n.createdAt DESC, n.id DESC
		""")
	List<NotificationHistory> findByUserId(@Param("userId") Long userId, Pageable pageable);

	@Query("""
		SELECT n FROM NotificationHistory n
		WHERE n.user.id = :userId
		AND (n.createdAt < :createdAt OR (n.createdAt = :createdAt AND n.id < :id))
		ORDER BY n.createdAt DESC, n.id DESC
		""")
	List<NotificationHistory> findByUserIdWithCursor(
		@Param("userId") Long userId,
		@Param("createdAt") LocalDateTime createdAt,
		@Param("id") Long id,
		Pageable pageable
	);
}
