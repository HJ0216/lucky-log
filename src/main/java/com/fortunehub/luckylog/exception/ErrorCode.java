package com.fortunehub.luckylog.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // 회원가입
  DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "📧 이미 사용 중인 이메일입니다!"),
  DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "✨ 이미 사용 중인 닉네임입니다!"),

  // Gemini
  GEMINI_EMPTY_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "❓ Gemini 응답이 비어있습니다."),
  GEMINI_OVERLOAD(HttpStatus.SERVICE_UNAVAILABLE, "😵 Gemini API 과부하 상태입니다. 잠시 후 다시 시도해주세요."),
  GEMINI_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "😮 Gemini API 호출 중 예기치 못한 오류가 발생했습니다."),
  GEMINI_RESPONSE_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "🔮 Gemini 응답 파싱 중 오류가 발생했습니다."),

  // AI 타입
  UNSUPPORTED_AI_TYPE(HttpStatus.BAD_REQUEST, "🤖 지원되지 않는 AI 타입입니다.");  private final HttpStatus status;

  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }
}
