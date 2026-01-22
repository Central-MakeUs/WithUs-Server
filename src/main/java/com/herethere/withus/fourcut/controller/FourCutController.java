package com.herethere.withus.fourcut.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.herethere.withus.common.apiresponse.ApiResponse;
import com.herethere.withus.fourcut.api.FourCutApi;
import com.herethere.withus.fourcut.dto.response.FourCutCursorResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FourCutController implements FourCutApi {

	@Override
	public ResponseEntity<ApiResponse<FourCutCursorResponse>> getFourCutsByCursor(int size, String cursor) {

		return null;
	}
}
