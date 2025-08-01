package com.fortunehub.luckylog.dto.request.auth;

import com.fortunehub.luckylog.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "사용자 생성을 위한 요청")
public record UserCreateRequest(
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 50, message = "이메일은 50자를 초과할 수 없습니다.")
    @Schema(description = "사용자 로그인 이메일", example = "chrome123@naver.com")
    String email,

    @NotBlank(message = "닉네임은 필수입니다.") // 클라이언트가 nickname 필드를 아예 보내지 않거나 null일 경우
    @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하여야 합니다.")
    @Schema(description = "서비스에서 사용할 별칭", example = "nick")
    String nickname,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Schema(description = "비밀번호 (8자 이상)", example = "password123")
    String password
) {

  public User toEntity(String encodedPassword) {
    return new User(email, nickname, encodedPassword);
  }
}
