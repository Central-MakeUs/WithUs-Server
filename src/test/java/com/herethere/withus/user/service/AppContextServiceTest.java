package com.herethere.withus.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.ForbiddenException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.repository.CoupleRepository;
import com.herethere.withus.fixture.CoupleFixture;
import com.herethere.withus.fixture.UserFixture;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppContextService 단위 테스트")
class AppContextServiceTest {

	private static final Long USER_ID = 1L;
	@Mock
	private UserRepository userRepository;
	@Mock
	private CoupleRepository coupleRepository;
	@InjectMocks
	private AppContextService appContextService;

	@BeforeEach
	void setUpSecurityContext() {
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(USER_ID, null, List.of())
		);
	}

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("SecurityContext 의 userId 로 사용자를 조회해 반환한다")
	void getCurrentUser_returnsUser_whenFound() {
		User user = UserFixture.activeInitializedUserWithId(USER_ID);
		given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));

		User result = appContextService.getCurrentUser();

		assertThat(result).isEqualTo(user);
	}

	@Test
	@DisplayName("userId 에 해당하는 사용자가 없으면 NotFoundException 이 발생한다")
	void getCurrentUser_throwsNotFoundException_whenUserNotFound() {
		given(userRepository.findById(USER_ID)).willReturn(Optional.empty());

		assertThatThrownBy(() -> appContextService.getCurrentUser())
			.isInstanceOf(NotFoundException.class);
	}

	@Test
	@DisplayName("ACTIVE 상태인 사용자를 정상적으로 반환한다")
	void getActiveUser_returnsUser_whenActive() {
		User user = UserFixture.activeInitializedUserWithId(USER_ID);
		given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));

		User result = appContextService.getActiveUser();

		assertThat(result).isEqualTo(user);
	}

	@Test
	@DisplayName("DELETED 상태인 사용자를 조회하면 ForbiddenException 이 발생한다")
	void getActiveUser_throwsForbiddenException_whenDeleted() {
		User user = UserFixture.deletedUserWithId(USER_ID);
		given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));

		assertThatThrownBy(() -> appContextService.getActiveUser())
			.isInstanceOf(ForbiddenException.class);
	}

	@Test
	@DisplayName("ACTIVE 이고 초기 설정이 완료된 사용자를 정상적으로 반환한다")
	void getInitializedAndActiveUser_returnsUser_whenActiveAndInitialized() {
		User user = UserFixture.activeInitializedUserWithId(USER_ID);
		given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));

		User result = appContextService.getInitializedAndActiveUser();

		assertThat(result).isEqualTo(user);
	}

	@Test
	@DisplayName("DELETED 상태인 사용자를 조회하면 ForbiddenException 이 발생한다")
	void getInitializedAndActiveUser_throwsForbiddenException_whenDeleted() {
		User user = UserFixture.deletedUserWithId(USER_ID);
		given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));

		assertThatThrownBy(() -> appContextService.getInitializedAndActiveUser())
			.isInstanceOf(ForbiddenException.class);
	}

	@Test
	@DisplayName("초기 설정이 완료되지 않은 사용자를 조회하면 ConflictException 이 발생한다")
	void getInitializedAndActiveUser_throwsConflictException_whenNotInitialized() {
		User user = UserFixture.activeUninitializedUserWithId(USER_ID);
		given(userRepository.findById(USER_ID)).willReturn(Optional.of(user));

		assertThatThrownBy(() -> appContextService.getInitializedAndActiveUser())
			.isInstanceOf(ConflictException.class);
	}

	@Test
	@DisplayName("활성 커플이 존재하면 해당 커플을 반환한다")
	void getActiveCoupleRequired_returnsCouple_whenFound() {
		User user = UserFixture.activeInitializedUserWithId(USER_ID);
		User partner = UserFixture.activeInitializedUserWithId(2L);
		Couple couple = CoupleFixture.activeCoupleWithId(10L, user, partner);
		given(coupleRepository.findActiveCouple(user)).willReturn(Optional.of(couple));

		Couple result = appContextService.getActiveCoupleRequired(user);

		assertThat(result).isEqualTo(couple);
	}

	@Test
	@DisplayName("활성 커플이 없으면 NotFoundException 이 발생한다")
	void getActiveCoupleRequired_throwsNotFoundException_whenCoupleNotFound() {
		User user = UserFixture.activeInitializedUserWithId(USER_ID);
		given(coupleRepository.findActiveCouple(user)).willReturn(Optional.empty());

		assertThatThrownBy(() -> appContextService.getActiveCoupleRequired(user))
			.isInstanceOf(NotFoundException.class);
	}

	@Test
	@DisplayName("활성 커플이 있으면 Optional 에 커플을 담아 반환한다")
	void findActiveCouple_returnsCouple_whenPresent() {
		User user = UserFixture.activeInitializedUserWithId(USER_ID);
		User partner = UserFixture.activeInitializedUserWithId(2L);
		Couple couple = CoupleFixture.activeCoupleWithId(10L, user, partner);
		given(coupleRepository.findActiveCouple(user)).willReturn(Optional.of(couple));

		Optional<Couple> result = appContextService.findActiveCouple(user);

		assertThat(result).isPresent().contains(couple);
	}

	@Test
	@DisplayName("활성 커플이 없으면 Optional.empty() 를 반환한다")
	void findActiveCouple_returnsEmpty_whenNoCouple() {
		User user = UserFixture.activeInitializedUserWithId(USER_ID);
		given(coupleRepository.findActiveCouple(user)).willReturn(Optional.empty());

		Optional<Couple> result = appContextService.findActiveCouple(user);

		assertThat(result).isEmpty();
	}
}
