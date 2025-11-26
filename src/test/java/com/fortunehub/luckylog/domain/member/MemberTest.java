package com.fortunehub.luckylog.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import com.fortunehub.luckylog.dto.request.auth.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("회원 Entity")
class MemberTest {

  @Test
  @DisplayName("이메일이 소문자로 변환되고 공백이 제거된다")
  void normalizeEmail_WhenUnnormalizedEmail_ThenReturnsTrimmedLowercaseEmail() {
    // given
    SignupRequest request = new SignupRequest("  EMAIL@EMAIL.COM  ", "PassWord147@", "솜사탕");

    // when
    Member member = Member.from(request, "EncodedPassword147@");

    // then
    assertThat(member.getEmail()).isEqualTo("email@email.com");
  }
}