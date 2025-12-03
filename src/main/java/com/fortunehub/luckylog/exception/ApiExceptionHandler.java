package com.fortunehub.luckylog.exception;

import com.fortunehub.luckylog.dto.response.common.ErrorResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.fortunehub.luckylog.controller.api")
public class ApiExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity handleValidation(
      MethodArgumentNotValidException ex
  ) {
    log.warn("[Validation 실패] {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> {
      errors.put(error.getField(), error.getDefaultMessage());
      log.warn("  - {}: {}", error.getField(), error.getDefaultMessage());
    });

    ErrorResponse response = ErrorResponse.builder()
                                          .code("VALIDATION_FAILED")
                                          .message("입력값 검증에 실패했습니다.")
                                          .timestamp(LocalDateTime.now())
                                          .details(errors)
                                          .build();

    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(CustomException.class)
  public ResponseEntity handleCustomException(CustomException ex) {
    log.warn("[CustomException] code={}, message={}",
        ex.getErrorCode(), ex.getMessage());

    ErrorCode errorCode = ex.getErrorCode();

    ErrorResponse response = ErrorResponse.builder()
                                          .code(errorCode.name())
                                          .message(errorCode.getMessage())
                                          .timestamp(LocalDateTime.now())
                                          .build();

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity handleException(Exception ex) {
    log.error("[예상치 못한 예외]", ex);

    ErrorResponse response = ErrorResponse.builder()
                                          .code("INTERNAL_SERVER_ERROR")
                                          .message("서버 오류가 발생했습니다.")
                                          .timestamp(LocalDateTime.now())
                                          .build();

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(response);
  }
}
