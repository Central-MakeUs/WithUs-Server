package com.herethere.withus.user.api;

import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/users")
@Tag(name = "회원 API")
public interface UserApi {

}
