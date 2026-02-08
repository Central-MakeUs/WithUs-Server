package com.herethere.withus.couple.dto.response;

import java.time.LocalDate;

public record CoupleProfileResponse(UserProfile meProfile, UserProfile partnerProfile) {

	public record UserProfile(String nickname, LocalDate birthday, String profileImageUrl) {

	}
}
