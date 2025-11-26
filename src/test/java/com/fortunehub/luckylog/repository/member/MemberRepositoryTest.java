package com.fortunehub.luckylog.repository.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fortunehub.luckylog.domain.member.Member;
import java.util.Optional;
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
  void existsByEmail_WhenMemberSaved_ThenReturnsTrue() {
    // given
    Member member = new Member(TEST_EMAIL, TEST_PASSWORD, TEST_NICKNAME);
    memberRepository.save(member);

    // when & then
    assertThat(memberRepository.existsByEmail(TEST_EMAIL)).isTrue();
    assertThat(memberRepository.existsByEmail("other@email.com")).isFalse();
  }

  @Test
  @DisplayName("대소문자가 다른 이메일도 중복으로 처리된다")
  void save_WhenEmailDiffersByCase_ThenThrowsException() {
    // given
    Member member1 = new Member("Test@Email.com", TEST_PASSWORD, "닉네임1");
    memberRepository.save(member1);

    Member member2 = new Member("test@email.com", TEST_PASSWORD, "닉네임2");

    // when & then
    assertThatThrownBy(() -> {
      memberRepository.saveAndFlush(member2);
    }).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("중복된 이메일로 저장 시 예외가 발생한다")
  void save_WhenEmailDuplicated_ThenThrowsException() {
    // given
    Member member = new Member(TEST_EMAIL, TEST_PASSWORD, TEST_NICKNAME);
    memberRepository.save(member);
    Member duplicate = new Member(TEST_EMAIL, TEST_PASSWORD, "other_nickname");

    // when & then
    assertThatThrownBy(() -> {
      memberRepository.saveAndFlush(duplicate);
    }).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("닉네임으로 회원 존재 여부를 확인할 수 있다")
  void existsByNickname_WhenMemberSaved_ThenReturnsTrue() {
    // given
    Member member = new Member(TEST_EMAIL, TEST_PASSWORD, TEST_NICKNAME);
    memberRepository.save(member);

    // when & then
    assertThat(memberRepository.existsByNickname(TEST_NICKNAME)).isTrue();
    assertThat(memberRepository.existsByNickname("other_nickname")).isFalse();
  }

  @Test
  @DisplayName("닉네임이 null인 회원을 저장할 수 있다")
  void save_WhenNicknameNull_ThenSavesSuccessfully() {
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
  @DisplayName("중복된 닉네임으로 저장 시 예외가 발생한다")
  void save_WhenNicknameDuplicated_ThenThrowsException() {
    // given
    Member member = new Member(TEST_EMAIL, TEST_PASSWORD, TEST_NICKNAME);
    memberRepository.save(member);
    Member duplicate = new Member("other@email.com", TEST_PASSWORD, TEST_NICKNAME);

    // when & then
    assertThatThrownBy(() -> {
      memberRepository.saveAndFlush(duplicate);
    }).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("이메일로 회원 조회 시 회원을 반환한다")
  void findByEmail_WhenMemberExists_ThenReturnsMember() {
    // given
    Member member = new Member(TEST_EMAIL, TEST_PASSWORD, TEST_NICKNAME);
    memberRepository.save(member);

    // when
    Optional<Member> result = memberRepository.findByEmail(TEST_EMAIL);

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo(TEST_EMAIL);
    assertThat(result.get().getNickname()).isEqualTo(TEST_NICKNAME);
  }

  @Test
  @DisplayName("존재하지 않는 이메일로 조회 시 빈 Optional을 반환한다")
  void findByEmail_WhenMemberNotExists_ThenReturnsEmpty() {
    // when
    Optional<Member> result = memberRepository.findByEmail(TEST_EMAIL);

    // then
    assertThat(result).isEmpty();
  }
}
