package com.herethere.withus.user.domain;

import com.herethere.withus.auth.domain.OAuthProviderType;
import com.herethere.withus.common.baseentity.BaseEntity;
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

	@Column(name = "profile_image_url", length = 2048)
	private String profileImageUrl;
}
