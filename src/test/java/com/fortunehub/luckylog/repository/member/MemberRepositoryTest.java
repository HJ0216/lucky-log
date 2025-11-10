package com.fortunehub.luckylog.repository.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fortunehub.luckylog.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@DisplayName("회원 Repository")
class MemberRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  private static final String TEST_EMAIL = "lucky@email.com";
  private static final String TEST_PASSWORD = "encodedPassword123";
  private static final String TEST_NICKNAME = "솜사탕";

  @Test
  @DisplayName("이메일로 회원 존재 여부를 확인할 수 있다")
  void existsByEmail(){
    // given
    Member member = new Member(TEST_EMAIL, TEST_PASSWORD, TEST_NICKNAME);
    memberRepository.save(member);

    // when & then
    assertThat(memberRepository.existsByEmail(TEST_EMAIL)).isTrue();
    assertThat(memberRepository.existsByEmail("other@email.com")).isFalse();
  }

  @Test
  @DisplayName("여러 회원을 저장하고 이메일로 존재 여부를 확인할 수 있다")
  void existsByEmail_MultipleMembers() {
    // given
    memberRepository.save(new Member("user1@email.com", TEST_PASSWORD, "닉네임1"));
    memberRepository.save(new Member("user2@email.com", TEST_PASSWORD, "닉네임2"));
    memberRepository.save(new Member("user3@email.com", TEST_PASSWORD, "닉네임3"));

    // when & then
    assertThat(memberRepository.existsByEmail("user1@email.com")).isTrue();
    assertThat(memberRepository.existsByEmail("user2@email.com")).isTrue();
    assertThat(memberRepository.existsByEmail("user3@email.com")).isTrue();
    assertThat(memberRepository.existsByEmail("user4@email.com")).isFalse();
  }

  @Test
  @DisplayName("닉네임으로 회원 존재 여부를 확인할 수 있다")
  void existsByNickname() {
    // given
    Member member = new Member(TEST_EMAIL, TEST_PASSWORD, TEST_NICKNAME);
    memberRepository.save(member);

    // when & then
    assertThat(memberRepository.existsByNickname(TEST_NICKNAME)).isTrue();
    assertThat(memberRepository.existsByNickname("other_nickname")).isFalse();
  }

  @Test
  @DisplayName("동일한 이메일로 저장하면 예외가 발생한다")
  void duplicateEmail_ThrowsException() {
    // given
    Member member = new Member(TEST_EMAIL, TEST_PASSWORD, TEST_NICKNAME);
    memberRepository.save(member);
    Member duplicate = new Member(TEST_EMAIL, TEST_PASSWORD, "other_nickname");

    // when & then
    assertThatThrownBy(() -> {
      memberRepository.save(duplicate); // 영속성 컨텍스트에만 저장 (메모리)
    }).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("동일한 닉네임으로 저장하면 예외가 발생한다")
  void duplicateNickname_ThrowsException() {
    // given
    Member member = new Member(TEST_EMAIL, TEST_PASSWORD, TEST_NICKNAME);
    memberRepository.save(member);
    Member duplicate = new Member("other@email.com", TEST_PASSWORD, TEST_NICKNAME);

    // when & then
    assertThatThrownBy(() -> {
      memberRepository.save(duplicate);
    }).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("같은 이메일과 닉네임이 모두 중복되면 예외가 발생한다")
  void duplicateEmailAndNickname_ThrowsException() {
    // given
    Member member = new Member(TEST_EMAIL, TEST_PASSWORD, TEST_NICKNAME);
    memberRepository.save(member);

    Member duplicate = new Member(TEST_EMAIL, TEST_PASSWORD, TEST_NICKNAME);

    // when & then
    assertThatThrownBy(() -> {
      memberRepository.save(duplicate);
    }).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("닉네임이 null인 회원을 저장할 수 있다")
  void saveWithNullNickname() {
    // given
    Member member = new Member(TEST_EMAIL, TEST_PASSWORD, null);

    // when
    Member saved = memberRepository.save(member);
    // IDENTITY는 save() 호출 시 즉시 INSERT 실행
    // IDENTITY, SEQUENCE, TABLE 모두 flush() 없어도 ID가 생성됨

    // then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getNickname()).isNull();
  }

  @Test
  @DisplayName("대소문자가 다른 이메일은 같은 이메일로 인식한다")
  void emailCaseSensitive() {
    // given
    Member member1 = new Member("Test@Email.com", TEST_PASSWORD, "닉네임1");
    memberRepository.save(member1);

    Member member2 = new Member("test@email.com", TEST_PASSWORD, "닉네임2");

    // when & then
    assertThatThrownBy(() -> {
      memberRepository.save(member2);
    }).isInstanceOf(DataIntegrityViolationException.class);
  }
}
