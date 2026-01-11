package com.herethere.withus.s3.dto.response;

public record PresignedUrlResponse(String postUrl, String imageKey, String accessUrl) {
}
