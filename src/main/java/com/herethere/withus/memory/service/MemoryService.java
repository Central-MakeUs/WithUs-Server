package com.herethere.withus.memory.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.exception.BadRequestException;
import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.ErrorCode;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.keyword.domain.KeywordRecord;
import com.herethere.withus.keyword.repository.KeywordRecordRepository;
import com.herethere.withus.memory.domain.CustomMemory;
import com.herethere.withus.memory.domain.MemoryType;
import com.herethere.withus.memory.domain.WeekMemory;
import com.herethere.withus.memory.dto.internal.WeekRange;
import com.herethere.withus.memory.dto.request.CustomMemoryCreateRequest;
import com.herethere.withus.memory.dto.request.MemoryCreateRequest;
import com.herethere.withus.memory.dto.response.MemoryDetailResponse;
import com.herethere.withus.memory.dto.response.MonthMemoryResponse;
import com.herethere.withus.memory.dto.response.WeekMemoryCreateResponse;
import com.herethere.withus.memory.repository.CustomMemoryRepository;
import com.herethere.withus.memory.repository.WeekMemoryRepository;
import com.herethere.withus.question.domain.QuestionPicture;
import com.herethere.withus.question.repository.QuestionPictureRepository;
import com.herethere.withus.s3.domain.ImageType;
import com.herethere.withus.s3.service.S3Service;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.service.AppContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemoryService {
	private final WeekMemoryRepository weekMemoryRepository;
	private final CustomMemoryRepository customMemoryRepository;
	private final QuestionPictureRepository questionPictureRepository;
	private final KeywordRecordRepository keywordRecordRepository;
	private final MemoryMapper memoryMapper;
	private final AppContextService appContextService;
	private final S3Service s3Service;

	@Transactional(readOnly = true)
	public MonthMemoryResponse getMonthMemories(int monthKey) {
		User user = appContextService.getInitializedAndActiveUser();
		Couple couple = appContextService.getActiveCoupleRequired(user);
		User partner = couple.getPartner(user);

		List<WeekRange> weeks = calculateWeeksOfMonthUntilToday(monthKey);
		LocalDate startDate = weeks.getFirst().startDate();
		LocalDate endDate = weeks.getLast().endDate();

		List<WeekMemory> weekMemories = weekMemoryRepository.findAllByCoupleAndMonthKey(couple, monthKey);
		List<QuestionPicture> questionPictures = questionPictureRepository.findAllByCoupleInPeriod(couple.getId(),
			startDate, endDate);
		List<KeywordRecord> keywordRecords = keywordRecordRepository.findAllByCoupleInPeriod(couple.getId(), startDate,
			endDate);

		Stream<MonthMemoryResponse.MemorySummary> weekSummaries = weeks.stream()
			.map(range -> createMemorySummary(range, user, partner, weekMemories, questionPictures, keywordRecords))
			.filter(Objects::nonNull);

		Stream<MonthMemoryResponse.MemorySummary> customSummaries = customMemoryRepository.findAllByCoupleAndMonthKey(
				couple, monthKey).stream()
			.map(memoryMapper::toCustomMemorySummary);

		List<MonthMemoryResponse.MemorySummary> summaries = Stream.concat(weekSummaries, customSummaries)
			.sorted(Comparator.comparing(MonthMemoryResponse.MemorySummary::createdAt).reversed())
			.toList();

		return new MonthMemoryResponse(monthKey, summaries);
	}

	@Transactional
	public WeekMemoryCreateResponse createMemory(MemoryCreateRequest request, LocalDate weekEndDate) {
		User user = appContextService.getInitializedAndActiveUser();
		Couple couple = appContextService.getActiveCoupleRequired(user);

		// 1. 토요일인지 확인
		if (weekEndDate.getDayOfWeek() != DayOfWeek.SATURDAY) {
			throw new BadRequestException(ErrorCode.WRONG_DATE);
		}

		// 2. 오늘 이전(과거 또는 오늘)인지 확인
		if (weekEndDate.isAfter(LocalDate.now())) {
			throw new BadRequestException(ErrorCode.FUTURE_DATE_NOT_ALLOWED);
		}

		// 3. 해당 주차(weekEndDate)에 이미 만들어진 메모리가 있는지 확인
		boolean exists = weekMemoryRepository.existsByCoupleAndWeekEndDate(couple, weekEndDate);
		if (exists) {
			throw new ConflictException(ErrorCode.MEMORY_ALREADY_UPLOADED);
		}

		String imageKey = s3Service.processImagePublish(request.imageKey(), user.getId(), ImageType.MEMORY);

		WeekMemory weekMemory = WeekMemory.create(user, couple, imageKey, weekEndDate);
		weekMemoryRepository.save(weekMemory);

		return new WeekMemoryCreateResponse(MemoryType.WEEK_MEMORY, weekEndDate);
	}

	@Transactional
	public void createCustomMemory(CustomMemoryCreateRequest request) {
		User user = appContextService.getInitializedAndActiveUser();
		Couple couple = appContextService.getActiveCoupleRequired(user);

		String imageKey = s3Service.processImagePublish(request.imageKey(), user.getId(), ImageType.MEMORY);
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
		Integer monthKey = today.getYear() * 100 + today.getMonthValue();

		CustomMemory customMemory = CustomMemory.builder()
			.user(user)
			.couple(couple)
			.title(request.title())
			.imageKey(imageKey)
			.monthKey(monthKey)
			.build();

		customMemoryRepository.save(customMemory);
	}

	@Transactional(readOnly = true)
	public MemoryDetailResponse getMemoryDetail(MemoryType memoryType, LocalDate weekEndDate, Long targetId) {
		User user = appContextService.getInitializedAndActiveUser();
		Couple couple = appContextService.getActiveCoupleRequired(user);

		Optional<MemoryDetailResponse> response = Optional.empty();

		if (memoryType == MemoryType.WEEK_MEMORY) {
			response = weekMemoryRepository.findByCoupleAndWeekEndDate(couple, weekEndDate)
				.map(m -> new MemoryDetailResponse(memoryMapper.generateTitle(m),
					s3Service.createOriginImageUrl(m.getImageKey())
				));
		} else if (memoryType == MemoryType.CUSTOM_MEMORY) {
			response = customMemoryRepository.findById(targetId)
				.map(m -> new MemoryDetailResponse(m.getTitle(),
					s3Service.createOriginImageUrl(m.getImageKey())
				));
		}

		return response.orElseThrow(() -> new NotFoundException(MEMORY_NOT_FOUND));
	}

	private MonthMemoryResponse.MemorySummary createMemorySummary(WeekRange range, User user, User partner,
		List<WeekMemory> weekMemories, List<QuestionPicture> questionPictures, List<KeywordRecord> keywordRecords) {
		LocalDate start = range.startDate();
		LocalDate end = range.endDate();
		Optional<WeekMemory> existingMemory = weekMemories.stream()
			.filter(m -> m.checkWeekRange(start, end))
			.findFirst();

		if (existingMemory.isPresent()) {
			return memoryMapper.toMemorySummary(existingMemory.get());
		}

		List<String> myImageKeys = getUserUploadedImageKeysInRange(user, questionPictures, keywordRecords, start, end);
		List<String> partnerImageKeys = getUserUploadedImageKeysInRange(partner, questionPictures, keywordRecords,
			start, end);

		if (myImageKeys.size() < 6 || partnerImageKeys.size() < 6) {
			// 개수가 6보다 적고, 아직 지나지 않았으면 UNAVAILABLE
			// 개수가 6보다 적고, 이미 지났으면, 안보여준다.
			LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
			if (now.isBefore(start) || now.isAfter(end)) {
				return null;
			}
			return memoryMapper.toUnavailableMemorySummary(end);
		}
		List<String> result = combineRandomImages(myImageKeys, partnerImageKeys, 6);
		return memoryMapper.toNeedCreateMemorySummary(result, end);
	}

	private List<String> getUserUploadedImageKeysInRange(User user, List<QuestionPicture> questionPictures,
		List<KeywordRecord> keywordRecords, LocalDate start, LocalDate end) {
		Long userId = user.getId();

		Stream<String> questionKeys = questionPictures.stream()
			.filter(q -> q.getUser().getId().equals(userId))
			.filter(q -> q.getCoupleQuestion().isDateInRange(start, end))
			.map(QuestionPicture::getImageKey);

		Stream<String> keywordKeys = keywordRecords.stream()
			.filter(k -> k.getUser().getId().equals(userId))
			.filter(k -> k.isDateInRange(start, end))
			.map(KeywordRecord::getImageKey);

		return Stream.concat(questionKeys, keywordKeys)
			.toList();
	}

	private List<String> combineRandomImages(List<String> list1, List<String> list2, int count) {
		Collections.shuffle(list1);
		Collections.shuffle(list2);

		List<String> result = new ArrayList<>();
		result.addAll(list1.subList(0, count));
		result.addAll(list2.subList(0, count));
		return result;
	}

	private List<WeekRange> calculateWeeksOfMonthUntilToday(int monthKey) {
		int year = monthKey / 100;
		int month = monthKey % 100;

		LocalDate firstDay = LocalDate.of(year, month, 1);
		LocalDate lastDay = firstDay.with(TemporalAdjusters.lastDayOfMonth());
		LocalDate today = LocalDate.now(); // 오늘 날짜

		List<WeekRange> weeks = new ArrayList<>();
		// 첫 날이 속한 주의 일요일부터 시작
		LocalDate currentSunday = firstDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

		while (!currentSunday.isAfter(lastDay)) {
			LocalDate currentSaturday = currentSunday.plusDays(6);

			// 규칙 1: 끝나는 날(토요일)이 해당 달에 속해야 함
			// 규칙 2: 시작하는 날이 오늘보다 같거나 이전이어야 함
			if (currentSaturday.getMonthValue() == month && !currentSunday.isAfter(today)) {
				weeks.add(new WeekRange(currentSunday, currentSaturday));
			} else {
				break;
			}

			// 다음 주로 이동
			currentSunday = currentSunday.plusWeeks(1);
		}
		return weeks;
	}
}
