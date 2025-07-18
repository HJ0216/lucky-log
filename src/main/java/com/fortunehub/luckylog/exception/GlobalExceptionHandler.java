package com.fortunehub.luckylog.exception;

import com.fortunehub.luckylog.dto.response.ErrorResponse;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
    String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                            .map(error -> error.getField() + ": " + error.getDefaultMessage())
                            .collect(Collectors.joining(", "));

    return ResponseEntity
        .badRequest()
        .body(new ErrorResponse("VALIDATION_ERROR", errorMessage));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
  }
}
