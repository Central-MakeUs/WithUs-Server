package com.herethere.withus.user.service;

import org.springframework.stereotype.Service;

import com.herethere.withus.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
}
