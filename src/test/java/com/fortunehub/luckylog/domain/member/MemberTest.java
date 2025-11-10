package com.fortunehub.luckylog.domain.member;

import static org.junit.jupiter.api.Assertions.*;

import com.fortunehub.luckylog.dto.request.auth.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("회원 Entity")
class MemberTest {

  @Test
  @DisplayName("이메일에 대문자가 있을 경우, 소문자로 변환한다")
  void normalizeEmail_ToLowerCase(){
    // given
    SignupRequest request = new SignupRequest("EMAIL@EMAIL.COM", "PassWord147@", "솜사탕");

    // when
    Member member = request.toEntity("EncodedPassword147@");

    // then
    assertEquals("email@email.com", member.getEmail());
  }

  @Test
  @DisplayName("이메일 앞 또는 뒤에 공백이 있을 경우 제거한다")
  void normalizeEmail_Trim(){
    // given
    SignupRequest request = new SignupRequest("  email@email.com  ", "PassWord147@", "솜사탕");

    // when
    Member member = request.toEntity("EncodedPassword147@");

    // then
    assertEquals("email@email.com", member.getEmail());
  }
}