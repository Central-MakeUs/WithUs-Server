package com.herethere.withus.couple.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import com.herethere.withus.common.baseentity.BaseEntity;
import com.herethere.withus.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "couple")
public class Couple extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_a_id", unique = true, nullable = false)
	private User userA;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_b_id", unique = true, nullable = false)
	private User userB;

	@Column(name = "anniversary_date")
	private LocalDate anniversaryDate;

	@Column(name = "question_time")
	private LocalTime questionTime;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 10, nullable = false)
	private CoupleStatus status;

	@Column(name = "last_question_date")
	private LocalDate lastQuestionDate;

	@Column(name = "last_question_index", nullable = false)
	private int lastQuestionIndex;

	public static Couple create(User userA, User userB) {
		Couple couple = Couple.builder()
			.userA(userA)
			.userB(userB)
			.status(CoupleStatus.PENDING)
			.lastQuestionIndex(0)
			.build();

		userA.setCoupleAsA(couple);
		userB.setCoupleAsB(couple);

		return couple;
	}

	public User getPartner(Long userId) {
		return userA.getId().equals(userId) ? userB : userA;
	}
}
