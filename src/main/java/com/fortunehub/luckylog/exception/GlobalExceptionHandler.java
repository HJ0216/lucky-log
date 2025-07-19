package com.fortunehub.luckylog.exception;

import com.fortunehub.luckylog.dto.response.ErrorResponse;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

    String code = "VALIDATION_ERROR";
    String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                            .map(error -> error.getField() + ": " + error.getDefaultMessage())
                            .collect(Collectors.joining(" | "));

    return ResponseEntity.badRequest()
                         .body(new ErrorResponse(code, errorMessage));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParam(
      MissingServletRequestParameterException ex) {

    String code = "MISSING_REQUIRED_PARAMETER";
    String message = String.format("필수 파라미터(%s)가 누락되었습니다.",
        ex.getParameterName());

    return ResponseEntity.badRequest()
                         .body(new ErrorResponse(code, message));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex) {

    String code = "INVALID_REQUEST_BODY";
    String message = "요청 본문이 누락되었거나 형식이 올바르지 않습니다.";

    return ResponseEntity.badRequest()
                         .body(new ErrorResponse(code, message));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {

    String code = "BAD_REQUEST";
    String message = ex.getMessage();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                         .body(new ErrorResponse(code, message));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body(new ErrorResponse("INTERNAL_ERROR", ex.getMessage()));
  }
}
