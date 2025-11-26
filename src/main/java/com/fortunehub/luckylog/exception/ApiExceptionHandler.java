package com.fortunehub.luckylog.exception;

import com.fortunehub.luckylog.dto.response.common.ApiResponse;
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
  public ResponseEntity<ApiResponse> handleValidation(
      MethodArgumentNotValidException ex
  ) {
    log.warn("[API Validation 실패] {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );

    return ResponseEntity.badRequest()
                         .body(
                             ApiResponse.error(ErrorCode.ARGUMENT_NOT_VALID.getMessage(), errors));
  }

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ApiResponse> handleCustomException(CustomException ex) {
    log.warn("[CustomException] code={}, message={}",
        ex.getErrorCode(), ex.getMessage());

    return ResponseEntity
        .status(ex.getErrorCode().getStatus())
        .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse> handleException(Exception ex) {
    log.error("[예상치 못한 예외]", ex);

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error(ErrorCode.SYSTEM_ERROR.getMessage()));
  }
}
