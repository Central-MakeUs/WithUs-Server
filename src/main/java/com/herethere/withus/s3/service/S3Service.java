package com.herethere.withus.s3.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.herethere.withus.common.security.SecurityUtil;
import com.herethere.withus.s3.domain.ImageType;
import com.herethere.withus.s3.dto.request.PresignedUrlRequest;
import com.herethere.withus.s3.dto.response.PresignedUrlResponse;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Presigner s3Presigner;
	private final String bucketName = "withus-cmc-s3";

	public PresignedUrlResponse createPresignedUrlResponse(PresignedUrlRequest request) {
		ImageType imageType = request.imageType();
		String imageKey = generateImageKey(imageType);
		String uploadUrl = createPutPresignedUrl(imageKey);
		String accessUrl = createGetPresignedUrl(imageKey);
		return new PresignedUrlResponse(uploadUrl, accessUrl, imageKey);
	}

	private String createGetPresignedUrl(String imageKey) {
		GetObjectRequest getObjectRequest = GetObjectRequest.builder()
			.bucket(bucketName)
			.key(imageKey)
			.build();

		GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(60))
			.getObjectRequest(getObjectRequest)
			.build();

		PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

		return presignedRequest.url().toString();
	}

	private String createPutPresignedUrl(String imageKey) {
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(imageKey)
			.contentType("image/jpeg")
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(60)) // 60분 지속
			.putObjectRequest(putObjectRequest)
			.build();

		PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

		return presignedRequest.url().toString();
	}

	private String generateImageKey(ImageType imageType) {
		String userId = String.valueOf(SecurityUtil.getCurrentUserId());
		String fileName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
		return String.join("/", "users", userId, imageType.name().toLowerCase(), fileName);
	}
}
