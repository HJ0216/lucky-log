package com.fortunehub.luckylog.fixture;

import com.fortunehub.luckylog.domain.member.Member;
import org.springframework.test.util.ReflectionTestUtils;

public class MemberFixture {

  public static Member activeMember(String email, String nickname) {
    return new Member(email, "encodedPassword", nickname);
  }

  public static Member inactiveMember(String email, String nickname) {
    Member member = new Member(email, "encodedPassword", nickname);

    ReflectionTestUtils.setField(member, "isActive", false);
    return member;
  }
}
