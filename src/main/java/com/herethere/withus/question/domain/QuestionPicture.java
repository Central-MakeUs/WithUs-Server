package com.herethere.withus.question.domain;

import com.herethere.withus.common.baseentity.BaseEntity;
import com.herethere.withus.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "question_picture",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_user_couple_question",
			columnNames = {"user_id", "couple_question_id"}
		)
	}
)
public class QuestionPicture extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "couple_question_id", nullable = false)
	private CoupleQuestion coupleQuestion;

	@Column(name = "imageKey", nullable = false, length = 255)
	private String imageKey;
}
