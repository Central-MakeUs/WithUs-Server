package com.herethere.withus.user.domain;

import static com.herethere.withus.common.exception.ErrorCode.*;

import com.herethere.withus.auth.domain.OAuthProviderType;
import com.herethere.withus.common.baseentity.BaseEntity;
import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.couple.domain.Couple;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "user")
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "couple_id")
	private Couple couple;

	@Column(name = "nickname", length = 50, nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(name = "provider", length = 10, nullable = false)
	private OAuthProviderType provider;

	@Column(name = "provider_id", nullable = false)
	private Long providerId;

	@Column(name = "is_initialized", nullable = false)
	private boolean isInitialized;

	@Column(name = "profile_image_key", length = 2048)
	private String profileImageKey;

	public void initializeProfile(String nickname, String profileImageKey) {
		this.nickname = nickname;
		this.profileImageKey = profileImageKey;
		isInitialized = true;
	}

	public void assignCouple(Couple couple) {
		if (this.couple != null) {
			throw new ConflictException(COUPLE_ALREADY_EXISTS);
		}
		this.couple = couple;
	}

}
