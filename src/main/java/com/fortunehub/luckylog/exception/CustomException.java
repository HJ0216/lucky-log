package com.fortunehub.luckylog.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

  private final ErrorCode errorCode;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage()); // getMessage()로 예외 메시지를 불러올 수 있게 하는 역할
    this.errorCode = errorCode;
  }

  public CustomException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }
}
