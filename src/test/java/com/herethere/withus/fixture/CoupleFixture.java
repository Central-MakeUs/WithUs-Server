package com.herethere.withus.fixture;

import org.springframework.test.util.ReflectionTestUtils;

import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.user.domain.User;

public class CoupleFixture {

	public static Couple activeCouple(User userA, User userB) {
		return Couple.create(userA, userB);
	}

	public static Couple activeCoupleWithId(Long id, User userA, User userB) {
		Couple couple = activeCouple(userA, userB);
		ReflectionTestUtils.setField(couple, "id", id);
		return couple;
	}
}
