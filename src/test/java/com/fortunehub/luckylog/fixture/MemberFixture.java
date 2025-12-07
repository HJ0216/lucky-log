package com.fortunehub.luckylog.fixture;

import com.fortunehub.luckylog.domain.member.Member;
import org.springframework.test.util.ReflectionTestUtils;

public class MemberFixture {

  private static final String DEFAULT_EMAIL = "lucky@email.com";
  private static final String DEFAULT_NICKNAME = "테스트 유저";
  private static final String DEFAULT_PASSWORD = "encodedPassword123";

  private MemberFixture() {
  }

  public static Member createMember() {
    return createMember(DEFAULT_EMAIL, DEFAULT_NICKNAME);
  }

  public static Member createMember(String email) {
    return createMember(email, DEFAULT_NICKNAME);
  }

  public static Member createMember(String email, String nickname) {
    return new Member(email, DEFAULT_PASSWORD, nickname);
  }

  public static Member createMemberWithId(Long id) {
    Member member = createMember();
    ReflectionTestUtils.setField(member, "id", id);
    return member;
  }

  public static Member createInactiveMember() {
    return createInactiveMember(DEFAULT_EMAIL, DEFAULT_NICKNAME);
  }

  public static Member createInactiveMember(String email, String nickname) {
    Member member = new Member(email, DEFAULT_PASSWORD, nickname);
    ReflectionTestUtils.setField(member, "isActive", false);

    return member;
  }

  public static Member createInactiveMemberWithId(Long id) {
    Member member = createInactiveMember();
    ReflectionTestUtils.setField(member, "id", id);
    return member;
  }
}
