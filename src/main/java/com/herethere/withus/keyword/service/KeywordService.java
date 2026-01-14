package com.herethere.withus.keyword.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.annotation.RequiresActiveCouple;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.domain.CoupleKeyword;
import com.herethere.withus.couple.repository.CoupleKeywordRepository;
import com.herethere.withus.keyword.domain.Keyword;
import com.herethere.withus.keyword.dto.response.CoupleKeywordsResponse;
import com.herethere.withus.keyword.dto.response.DefaultKeywordsResponse;
import com.herethere.withus.keyword.repository.KeywordRepository;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.service.UserContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeywordService {
	private final KeywordRepository keywordRepository;
	private final CoupleKeywordRepository coupleKeywordRepository;
	private final UserContextService userContextService;

	@Transactional(readOnly = true)
	public DefaultKeywordsResponse getDefaultKeywords() {
		List<Keyword> keywordList = keywordRepository.findAllByIsDefaultTrueOrderByDisplayOrderAsc();
		List<DefaultKeywordsResponse.KeywordInfo> keywordInfos = keywordList.stream()
			.map(k -> new DefaultKeywordsResponse.KeywordInfo(k.getId(), k.getContent(), k.getDisplayOrder()))
			.toList();
		return new DefaultKeywordsResponse(keywordInfos);
	}

	@Transactional(readOnly = true)
	@RequiresActiveCouple
	public CoupleKeywordsResponse getCoupleKeywords() {
		User user = userContextService.getCurrentUser();
		Couple couple = user.getCouple();

		List<CoupleKeyword> coupleKeywords = coupleKeywordRepository.findAllByCouple(couple);
		List<CoupleKeywordsResponse.CoupleKeywordInfo> coupleKeywordInfos = coupleKeywords.stream().map(c -> {
			Keyword keyword = c.getKeyword();
			return new CoupleKeywordsResponse.CoupleKeywordInfo(keyword.getId(), c.getId(), keyword.getContent());
		}).toList();

		return new CoupleKeywordsResponse(coupleKeywordInfos);
	}
}
