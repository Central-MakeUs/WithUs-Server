package com.herethere.withus.common.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LogAspect {

	// 1. 어디에 적용할 것인가? (Pointcut)
	// 컨트롤러와 서비스 패키지 하위의 모든 메서드를 대상으로 합니다.
	@Pointcut("execution(* com.herethere.withus..controller..*.*(..)) || execution(* com.herethere.withus..service..*.*(..))")
	private void cut() {}

	// 2. 어떤 로직을 수행할 것인가? (Advice)
	// @Around는 메서드 실행 전후를 모두 제어합니다.
	@Around("cut()")
	public Object aroundLog(ProceedingJoinPoint joinPoint) throws Throwable {

		// 메서드 정보 추출
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();

		log.info(">>>> [START] {}.{}() | Args: {}", className, methodName, Arrays.toString(args));

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		try {
			// 실제 대상 메서드 실행
			Object result = joinPoint.proceed();

			stopWatch.stop();
			log.info("<<<< [END] {}.{}() | Time: {}ms | Return: {}",
				className, methodName, stopWatch.getTotalTimeMillis(), result);

			return result;
		} catch (Exception e) {
			// 예외 발생 시 로그
			log.error("#### [ERROR] {}.{}() | Message: {}", className, methodName, e.getMessage());
			throw e;
		}
	}
}
