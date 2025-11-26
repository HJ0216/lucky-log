package com.fortunehub.luckylog.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // 회원가입
  DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "📧 이미 사용 중인 이메일입니다!"),
  DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "✨ 이미 사용 중인 닉네임입니다!"),
  INVALID_SIGNUP_DATA(HttpStatus.BAD_REQUEST, "❌ 유효하지 않은 회원가입 정보입니다!"),
  SIGNUP_SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "😲 회원가입에 실패하였습니다.\n잠시 후 다시 시도해주세요."),

  // 로그인
  LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "🔒 이메일 또는 비밀번호가 일치하지 않습니다"),
  LOGIN_SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "😲 로그인에 실패하였습니다.\n잠시 후 다시 시도해주세요."),

  // Gemini
  GEMINI_EMPTY_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "❓ Gemini 응답이 비어있습니다."),
  GEMINI_OVERLOAD(HttpStatus.SERVICE_UNAVAILABLE, "😵 Gemini API 과부하 상태입니다. 잠시 후 다시 시도해주세요."),
  GEMINI_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "😮 Gemini API 호출 중 예기치 못한 오류가 발생했습니다."),
  GEMINI_RESPONSE_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "🔮 Gemini 응답 파싱 중 오류가 발생했습니다."),

  // AI 타입
  UNSUPPORTED_AI_TYPE(HttpStatus.BAD_REQUEST, "🤖 지원되지 않는 AI 타입입니다."),

  // API Validation
  ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "😲 입력값이 올바르지 않습니다."),
  SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "😲 서버 오류가 발생했습니다."),

  // 운세 저장
  INVALID_MEMBER(HttpStatus.BAD_REQUEST, "👤 유효하지 않은 회원입니다."),
  DUPLICATE_FORTUNE_TITLE(HttpStatus.BAD_REQUEST, "📝 이미 동일한 이름의 운세가 저장되어 있습니다."),
  EXCEED_MAX_SAVE_COUNT(HttpStatus.BAD_REQUEST, "💾 저장 가능한 운세 개수를 초과했습니다."),
  FORTUNE_CATEGORY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "🔍 일부 운세 카테고리를 찾을 수 없습니다."),
  FORTUNE_SAVE_SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "😲 운세 저장에 실패하였습니다.\n잠시 후 다시 시도해주세요."),

  // 운세 생성 - FortuneResult
  MEMBER_INFO_REQUIRED(HttpStatus.BAD_REQUEST, "👤 회원 정보는 필수입니다."),
  FORTUNE_REQUEST_REQUIRED(HttpStatus.BAD_REQUEST, "📋 운세 저장 요청 정보는 필수입니다."),
  FORTUNE_YEAR_REQUIRED(HttpStatus.BAD_REQUEST, "📅 운세 결과 연도는 필수입니다."),
  FORTUNE_OPTION_REQUIRED(HttpStatus.BAD_REQUEST, "⚙️ 운세 옵션 정보는 필수입니다."),
  FORTUNE_RESPONSE_REQUIRED(HttpStatus.BAD_REQUEST, "🔮 운세 결과는 필수입니다."),
  BIRTH_INFO_REQUIRED(HttpStatus.BAD_REQUEST, "🎂 생년월일 정보는 필수입니다."),
  BIRTH_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "📆 생년월일은 필수입니다."),
  INVALID_BIRTH_DATE(HttpStatus.BAD_REQUEST, "❌ 유효하지 않은 생년월일입니다.");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }
}
