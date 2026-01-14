package com.herethere.withus.keyword.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.herethere.withus.keyword.domain.Keyword;
import com.herethere.withus.keyword.dto.response.DefaultKeywordsResponse;
import com.herethere.withus.keyword.repository.KeywordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeywordService {
	private final KeywordRepository keywordRepository;

	public DefaultKeywordsResponse getDefaultKeywords() {
		List<Keyword> keywordList = keywordRepository.findAllByIsDefaultTrueOrderByDisplayOrderAsc();
		List<DefaultKeywordsResponse.KeywordInfo> keywords = keywordList.stream()
			.map(k -> new DefaultKeywordsResponse.KeywordInfo(k.getId(), k.getContent(), k.getDisplayOrder()))
			.toList();
		return new DefaultKeywordsResponse(keywords);
	}
}
