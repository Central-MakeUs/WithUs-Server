package com.herethere.withus.user.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.herethere.withus.auth.domain.OAuthProviderType;
import com.herethere.withus.common.baseentity.BaseEntity;
import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.ErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "user", uniqueConstraints = {
	@UniqueConstraint(
		name = "uk_user_provider_provider_id_deleted_at",
		columnNames = {"provider_id", "provider", "deleted_at"}
	)
})public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nickname", length = 50, nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(name = "provider", length = 10, nullable = false)
	private OAuthProviderType provider;

	@Column(name = "provider_id", length = 50, nullable = false)
	private String providerId;

	@Column(name = "birthday")
	private LocalDate birthday;

	@Column(name = "is_initialized", nullable = false)
	private boolean isInitialized;

	@Column(name = "profile_image_key", length = 255)
	private String profileImageKey;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_status", nullable = false)
	private UserStatus userStatus;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public void updateProfile(String nickname, LocalDate birthday, String profileImageKey) {
		this.nickname = nickname;
		this.birthday = birthday;
		this.profileImageKey = profileImageKey;
	}

	public void updateProfile(String nickname, LocalDate birthday) {
		this.nickname = nickname;
		this.birthday = birthday;
	}

	public void completeOnboarding(String nickname, LocalDate birthday, String profileImageKey) {
		this.nickname = nickname;
		this.birthday = birthday;
		this.profileImageKey = profileImageKey;
		isInitialized = true;
	}

	public void withdraw() {
		if (userStatus == UserStatus.DELETED) {
			throw new ConflictException(ErrorCode.USER_ALREADY_DELETED);
		}
		nickname = "알 수 없음";
		birthday = null;
		profileImageKey = null;
		deletedAt = LocalDateTime.now();
		userStatus = UserStatus.DELETED;
	}

	public LocalDate getJoinDate() {
		return getCreatedAt().toLocalDate();
	}
}
