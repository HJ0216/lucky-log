package com.fortunehub.luckylog.dto.response.user;

import com.fortunehub.luckylog.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "사용자 정보 조회 결과를 담는 응답")
public record UserResponse(
    @Schema(description = "아이디", example = "1")
    Long id,

    @Schema(description = "로그인 이메일", example = "chrome123@naver.com")
    String email,

    @Schema(description = "닉네임", example = "nick")
    String nickname,

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile/default.jpg")
    String profileImageUrl,

    @Schema(description = "가입일시", example = "2024-01-15T10:30:00")
    LocalDateTime createdAt) {

  public static UserResponse from(User user) {
    return new UserResponse(
        user.getId(),
        user.getEmail(),
        user.getNickname(),
        user.getProfileImageUrl(),
        user.getCreatedAt());
  }
}
