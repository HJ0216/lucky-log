package com.fortunehub.luckylog.dto.response.common;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

  private final String code;
  private final String message;
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
  private final Object details;
}
