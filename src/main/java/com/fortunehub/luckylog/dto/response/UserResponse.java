package com.fortunehub.luckylog.dto.response;

import com.fortunehub.luckylog.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(
    @Schema(description = "사용자 로그인 이메일", example = "chrome123@naver.com")
    String email,
    @Schema(description = "서비스에서 사용할 별칭", example = "nick")
    String nickname) {

  public static UserResponse from(User user) {
    return new UserResponse(user.getEmail(), user.getNickname());
  }
}
