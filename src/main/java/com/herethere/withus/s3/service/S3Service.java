package com.herethere.withus.s3.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.herethere.withus.common.exception.BadRequestException;
import com.herethere.withus.common.exception.ForbiddenException;
import com.herethere.withus.common.security.SecurityUtil;
import com.herethere.withus.s3.domain.ImageType;
import com.herethere.withus.s3.dto.request.PresignedUrlRequest;
import com.herethere.withus.s3.dto.response.PresignedUrlResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
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

	public String createOriginImageUrl(String imageKey) {
		if (!StringUtils.hasText(imageKey)) {
			log.warn("이미지 키가 비어있어 URL을 생성할 수 없습니다.");
			return null;
		}

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

	public String createThumbnailImageUrl(String imageKey) {
		if (!StringUtils.hasText(imageKey)) {
			log.warn("이미지 키가 비어있어 URL을 생성할 수 없습니다.");
			return null;
		}

		// 1. 원본 키에서 썸네일 키로 변환 (origin -> thumb)
		// 예: images/origin/users/1/photo.jpg -> images/thumbnail/users/1/photo.jpg
		String thumbnailKey = imageKey.replace(FINAL_ORIGIN, FINAL_THUMB);

		// 2. S3에 썸네일 파일이 실제로 존재하는지 확인
		boolean exists = false;
		try {
			s3Client.headObject(HeadObjectRequest.builder()
				.bucket(bucketName)
				.key(thumbnailKey)
				.build());
			exists = true;
		} catch (S3Exception e) {
			// 파일이 없으면(404) exists는 false 유지
			if (e.statusCode() != 404) {
				throw e; // 404 이외의 에러는 밖으로 던짐
			}
		}

		// 3. 존재하면 썸네일 키로, 없으면 원본 키로 Presigned URL 생성
		String finalKey = exists ? thumbnailKey : imageKey;

		GetObjectRequest getObjectRequest = GetObjectRequest.builder()
			.bucket(bucketName)
			.key(finalKey)
			.build();

		GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(60))
			.getObjectRequest(getObjectRequest)
			.build();

		PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

		return presignedRequest.url().toString();
	}

	public String processImagePublish(String imageKey, Long userId, ImageType imageType) {
		validateUploadImageKey(imageKey, userId, imageType);
		String finalImageKey = imageKey.replace(TEMP_ORIGIN, FINAL_ORIGIN);
		moveObject(imageKey, finalImageKey);
		return finalImageKey;
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

		if (!imageKey.endsWith(".jpg")) {
			throw new BadRequestException(WRONG_IMAGE_FORMAT);
		}

		String expectedPrefix = "temp/origin/users/" + userId + "/" + imageType.getPath();
		if (!imageKey.startsWith(expectedPrefix)) {
			throw new ForbiddenException(ACCESS_DENIED);
		}
	}
}
