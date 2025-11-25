package com.fortunehub.luckylog.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.fortunehub.luckylog.controller.api")
public class ApiExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(
      MethodArgumentNotValidException ex
  ) {
    log.warn("[API Validation 실패] {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );

    return ResponseEntity.badRequest()
                         .body(Map.of(
                             "success", false,
                             "message", ErrorCode.ARGUMENT_NOT_VALID.getMessage(),
                             "errors", errors
                         ));

  }
}
