package com.fortunehub.luckylog.dto.request;

import com.fortunehub.luckylog.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 50, message = "이메일은 50자를 초과할 수 없습니다")
    String email,

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하여야 합니다")
    String nickname,

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    String password
) {

  public User toEntity(String encodedPassword) {
    return new User(email, nickname, encodedPassword);
  }
}
