package com.fortunehub.luckylog.controller.web.auth.form;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fortunehub.luckylog.dto.request.auth.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("회원가입 Form")
class SignupFormTest {

  @Test
  @DisplayName("닉네임 앞뒤 공백이 제거된다")
  void toRequest_WhenNicknameHasSpaces_ThenTrimsSpaces() {
    // given
    SignupForm form = new SignupForm();
    form.setEmail("test@email.com");
    form.setPassword("Pass123!");
    form.setConfirmPassword("Pass123!");
    form.setNickname("  테스터  ");

    // when
    SignupRequest request = SignupRequest.from(form);

    // then
    assertEquals("테스터", request.getNickname());
  }

  @Test
  @DisplayName("공백만 있는 닉네임은 null로 변환된다")
  void toRequest_WhenNicknameOnlySpaces_ThenReturnsNull() {
    // given
    SignupForm form = new SignupForm();
    form.setEmail("test@email.com");
    form.setPassword("Pass123!");
    form.setConfirmPassword("Pass123!");
    form.setNickname(" ");

    // when
    SignupRequest request = SignupRequest.from(form);

    // then
    assertNull(request.getNickname());
  }

  @Test
  @DisplayName("빈 문자열 닉네임은 null로 변환된다")
  void toRequest_WhenNicknameEmpty_ThenReturnsNull() {
    // given
    SignupForm form = new SignupForm();
    form.setEmail("test@email.com");
    form.setPassword("Pass123!");
    form.setConfirmPassword("Pass123!");
    form.setNickname("");

    // when
    SignupRequest request = SignupRequest.from(form);

    //then
    assertNull(request.getNickname());
  }

  @Test
  @DisplayName("null 닉네임은 null로 유지된다")
  void toRequest_WhenNicknameNull_ThenReturnsNull() {
    // given
    SignupForm form = new SignupForm();
    form.setEmail("test@email.com");
    form.setPassword("Pass123!");
    form.setConfirmPassword("Pass123!");
    // nickname 설정 X

    // when
    SignupRequest request = SignupRequest.from(form);

    // then
    assertNull(request.getNickname());
  }
}