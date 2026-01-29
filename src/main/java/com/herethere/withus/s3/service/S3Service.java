package com.herethere.withus.s3.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.herethere.withus.common.exception.BadRequestException;
import com.herethere.withus.common.exception.ForbiddenException;
import com.herethere.withus.common.security.SecurityUtil;
import com.herethere.withus.s3.domain.FileCategory;
import com.herethere.withus.s3.domain.ImageType;
import com.herethere.withus.s3.dto.request.PresignedUrlRequest;
import com.herethere.withus.s3.dto.response.PresignedUrlResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

	private static final String TEMP_ORIGIN = "temp/origin/";
	private static final String FINAL_ORIGIN = "images/origin/";
	private static final String TEMP_THUMB = "temp/thumbnail/";
	private static final String FINAL_THUMB = "images/thumbnail/";
	private final S3Presigner s3Presigner;
	private final S3Client s3Client;
	private final String bucketName = "withus-cmc-s3";

	public PresignedUrlResponse createPresignedUrlResponse(PresignedUrlRequest request) {
		ImageType imageType = request.imageType();
		String imageKey = generateImageKey(imageType);
		String uploadUrl = createPutPresignedUrl(imageKey);
		return new PresignedUrlResponse(uploadUrl, imageKey);
	}

	public String createGetPresignedUrl(String imageKey, FileCategory fileCategory) {
		imageKey = fileCategory.addPrefix(imageKey);

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

	public void processImagePublish(String imageKey, Long userId, ImageType imageType) {
		validateUploadImageKey(imageKey, userId, imageType);
		// 1. 원본 이동
		moveObject(TEMP_ORIGIN + imageKey, FINAL_ORIGIN + imageKey);

		// 2. 썸네일 이동 (없을 수 있음)
		try {
			// 존재 여부를 묻지 않고 일단 Move 시도
			moveObject(TEMP_THUMB + imageKey, FINAL_THUMB + imageKey);
		} catch (S3Exception e) {
			// 404 에러(파일 없음)인 경우, 람다가 아직 안 만든 것이니 로그만 남기고 통과
			if (e.statusCode() == 404) {
				log.warn("썸네일이 아직 생성되지 않았습니다. 원본으로 대체 응답 준비 필요: {}", imageKey);
			} else {
				throw e;
			}
		}
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
		return String.join("/", "temp", "origin", "users", userId, imageType.getPath(), fileName);
	}

	private void moveObject(String fromKey, String toKey) {
		// copy
		s3Client.copyObject(CopyObjectRequest.builder()
			.sourceBucket(bucketName)
			.sourceKey(fromKey)
			.destinationBucket(bucketName)
			.destinationKey(toKey)
			.build());

		// delete
		s3Client.deleteObject(DeleteObjectRequest.builder()
			.bucket(bucketName)
			.key(fromKey)
			.build());
	}

	private void validateUploadImageKey(String imageKey, Long userId, ImageType imageType) {
		if (imageKey == null || imageKey.isBlank() || imageKey.contains("..")) {
			throw new BadRequestException(INVALID_INPUT);
		}

		String expectedPrefix = "temp/origin/users/" + userId + "/" + imageType.getPath();
		if (!imageKey.startsWith(expectedPrefix)) {
			throw new ForbiddenException(ACCESS_DENIED);
		}
	}
}
