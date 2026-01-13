package com.herethere.withus.couple.dto.request;

import java.util.List;

public record CoupleInitializeRequest(List<Long> selectedKeywordIds, List<String> customKeywords) {
}
