package com.herethere.withus.keyword.domain;

import java.time.LocalDate;

import com.herethere.withus.common.baseentity.BaseEntity;
import com.herethere.withus.couple.domain.CoupleKeyword;
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
@Table(name = "keyword_record",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_keyword_record_user_date",
			columnNames = {"user_id", "couple_keyword_id", "date"}
		)
	}
)
public class KeywordRecord extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "couple_keyword_id", nullable = false)
	private CoupleKeyword coupleKeyword;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "image_key", nullable = false, length = 255)
	private String imageKey;
}
