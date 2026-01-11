package com.herethere.withus.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 메서드에 붙일 것임
@Retention(RetentionPolicy.RUNTIME) // 실행 시점에 동작
public @interface RequiresActiveCouple {
}
