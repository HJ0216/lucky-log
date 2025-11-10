package com.fortunehub.luckylog.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "📧 이미 사용 중인 이메일입니다!"),
  DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "✨ 이미 사용 중인 닉네임입니다!");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }
}
