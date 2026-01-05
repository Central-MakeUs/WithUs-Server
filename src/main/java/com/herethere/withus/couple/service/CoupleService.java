package com.herethere.withus.couple.service;

import org.springframework.stereotype.Service;

import com.herethere.withus.couple.repository.CoupleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoupleService {
	private final CoupleRepository coupleRepository;
}
