package com.fortunehub.luckylog.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이메일 중복 검사 결과를 담는 응답")
public record EmailCheckResponse(
    @Schema(description = "이메일 사용 가능 여부", example = "true")
    boolean available,
    @Schema(description = "결과 메시지", example = "사용 가능한 이메일입니다.")
    String message) {

}
