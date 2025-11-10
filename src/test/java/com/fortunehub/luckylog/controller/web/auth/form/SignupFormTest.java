package com.fortunehub.luckylog.controller.web.auth.form;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fortunehub.luckylog.dto.request.auth.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("회원가입 Form")
class SignupFormTest {

  @Test
  @DisplayName("닉네임 앞 또는 뒤에 공백이 있을 경우 제거한다")
  void toRequest_TrimNickname() {
    // given
    SignupForm form = new SignupForm();
    form.setEmail("test@email.com");
    form.setPassword("Pass123!");
    form.setConfirmPassword("Pass123!");
    form.setNickname("  테스터  ");

    // when
    SignupRequest request = form.toRequest();

    // then
    assertEquals("테스터", request.getNickname());
  }

  @Test
  @DisplayName("닉네임에 띄어쓰기만 있을 경우, null로 처리한다")
  void toRequest_BlankNickname() {
    // given
    SignupForm form = new SignupForm();
    form.setEmail("test@email.com");
    form.setPassword("Pass123!");
    form.setConfirmPassword("Pass123!");
    form.setNickname(" ");

    // when
    SignupRequest request = form.toRequest();

    // then
    assertNull(request.getNickname());
  }

  @Test
  @DisplayName("닉네임이 빈 문자열일 경우, null로 처리한다")
  void toRequest_EmptyNickname() {
    // given
    SignupForm form = new SignupForm();
    form.setEmail("test@email.com");
    form.setPassword("Pass123!");
    form.setConfirmPassword("Pass123!");
    form.setNickname("");

    // when
    SignupRequest request = form.toRequest();

    //then
    assertNull(request.getNickname());
  }

  @Test
  @DisplayName("닉네임이 null일 경우, null로 처리한다")
  void toRequest_NullNickname() {
    // given
    SignupForm form = new SignupForm();
    form.setEmail("test@email.com");
    form.setPassword("Pass123!");
    form.setConfirmPassword("Pass123!");
    // nickname 설정 X

    // when
    SignupRequest request = form.toRequest();

    // then
    assertNull(request.getNickname());
  }
}