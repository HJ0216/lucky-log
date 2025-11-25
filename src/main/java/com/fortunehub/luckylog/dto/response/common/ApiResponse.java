package com.fortunehub.luckylog.dto.response.common;

import lombok.Getter;

@Getter
public class ApiResponse<T> { // <T> : 이 클래스는 T라는 타입 변수를 사용

  private final boolean success;
  private final String message;
  private final T data; // 여기서 T를 쓸 수 있음

  private ApiResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data = data;
  }

  public static <T> ApiResponse<T> success(String message) { // 앞의 <T>: 이 메서드는 T라는 타입 변수를 사용, 뒤의 <T>: 사용 가능
    return new ApiResponse<>(true, message, null);
  }

  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<>(false, message, null);
  }

  public static <T> ApiResponse<T> error(String message, T data) {
    return new ApiResponse<>(false, message, data);
  }
}
