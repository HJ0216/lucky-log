package com.fortunehub.luckylog.domain.member;

import static org.junit.jupiter.api.Assertions.*;

import com.fortunehub.luckylog.dto.request.auth.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("회원 Entity")
class MemberTest {

  @Test
  @DisplayName("이메일은 소문자로 변환되고 앞뒤 공백이 제거된다")
  void normalizeEmail_ToLowerCase(){
    // given
    SignupRequest request = new SignupRequest("  EMAIL@EMAIL.COM  ", "PassWord147@", "솜사탕");

    // when
    Member member = request.toEntity("EncodedPassword147@");

    // then
    assertEquals("email@email.com", member.getEmail());
  }
}