package com.fortunehub.luckylog.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "비밀번호 변경을 위한 요청")
public record MyPasswordUpdateRequest(
    @NotBlank(message = "비필번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Schema(description = "비밀번호 (8자 이상)", example = "password123")
    String password) {
  
}
