package com.herethere.withus.s3.dto.request;

import com.herethere.withus.s3.domain.ImageType;

import jakarta.validation.constraints.NotNull;

public record PresignedUrlRequest(@NotNull(message = "이미지 타입은 필수입니다.") ImageType imageType) {
}
