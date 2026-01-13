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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	@Setter
	@OneToOne(mappedBy = "userA", fetch = FetchType.LAZY)
	private Couple coupleAsA;

	@Setter
	@OneToOne(mappedBy = "userB", fetch = FetchType.LAZY)
	private Couple coupleAsB;

	@Column(name = "nickname", length = 50, nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(name = "provider", length = 10, nullable = false)
	private OAuthProviderType provider;

	@Column(name = "provider_id", length = 50, nullable = false)
	private String providerId;

	@Column(name = "is_initialized", nullable = false)
	private boolean isInitialized;

	@Column(name = "profile_image_key", length = 255)
	private String profileImageKey;

	public void initializeProfile(String nickname, String profileImageKey) {
		this.nickname = nickname;
		this.profileImageKey = profileImageKey;
		isInitialized = true;
	}

	public Couple getCouple() {
		return coupleAsA != null ? coupleAsA : coupleAsB;
	}
}
