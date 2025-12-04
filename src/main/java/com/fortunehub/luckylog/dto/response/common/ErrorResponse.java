package com.fortunehub.luckylog.dto.response.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "에러 응답")
@Getter
@Builder
public class ErrorResponse {

  @Schema(description = "에러 코드", example = "VALIDATION_FAILED")
  private final String code;
  @Schema(description = "에러 메시지", example = "입력값 검증에 실패했습니다.")
  private final String message;
  @Schema(description = "에러 발생 시각", example = "2025-12-04T10:30:00")
  private final LocalDateTime timestamp;
  /**
   * 에러 상세 정보
   * - Validation 오류 시: Map<String, String> (필드명 -> 에러 메시지)
   * - 일반 오류 시: String 또는 null
   *
   * 예시:
   * {
   *   "email": "이메일 형식이 올바르지 않습니다",
   *   "password": "비밀번호는 8자 이상이어야 합니다"
   * }
   */
  @Schema(description = "상세 에러 정보 (필드별 에러 메시지)", nullable = true)
  private final Object details;
}
