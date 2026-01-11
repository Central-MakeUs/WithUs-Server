package com.herethere.withus.s3.dto.response;

public record PresignedUrlResponse(String uploadUrl, String accessUrl, String imageKey) {
}
