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
  private final Object details;
}
