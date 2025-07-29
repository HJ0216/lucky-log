package com.fortunehub.luckylog.dto.response;

import com.fortunehub.luckylog.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "내 프로필 조회 응답")
public record MyProfileResponse(
    @Schema(description = "내 계정 고유 번호", example = "1")
    Long id,

    @Schema(description = "내 이메일", example = "chrome123@naver.com")
    String email,

    @Schema(description = "내 닉네임", example = "nick")
    String nickname,

    @Schema(description = "내 프로필 이미지 URL", example = "https://example.com/profile/default.jpg")
    String profileImageUrl,

    @Schema(description = "내 계정 가입일시", example = "2024-01-15T10:30:00")
    LocalDateTime createdAt) {

  public static MyProfileResponse from(User user) {
    return new MyProfileResponse(
        user.getId(),
        user.getEmail(),
        user.getNickname(),
        user.getProfileImageUrl(),
        user.getCreatedAt());
  }
}
