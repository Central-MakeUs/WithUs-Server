package com.herethere.withus.scheduling;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.repository.CoupleRepository;
import com.herethere.withus.question.service.QuestionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CoupleQuestionScheduler {

	private final QuestionService questionService;
	private final CoupleRepository coupleRepository;

	@Scheduled(cron = "5 0 0 * * *", zone = "Asia/Seoul")
	public void processCoupleQuestions() {
		ZoneId seoul = ZoneId.of("Asia/Seoul");
		LocalDate today = LocalDate.now(seoul);

		List<Couple> couples = coupleRepository.findCouplesToProcess(today);

		for (Couple couple : couples) {
			try {
				// 2. 개별 커플 처리를 독립된 트랜잭션으로 실행
				questionService.processCoupleQuestions(couple, today);
			} catch (Exception e) {
				log.error("커플 ID {} 처리 중 오류 발생: {}", couple.getId(), e.getMessage());
			}
		}
	}
}
