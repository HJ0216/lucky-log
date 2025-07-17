package com.fortunehub.luckylog.dto.response;

import com.fortunehub.luckylog.domain.User;

public record UserResponse(String email, String nickname) {

  public static UserResponse from(User user) {
    return new UserResponse(user.getEmail(), user.getNickname());
  }
}
