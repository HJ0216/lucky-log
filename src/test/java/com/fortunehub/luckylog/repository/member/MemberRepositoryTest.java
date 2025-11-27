package com.fortunehub.luckylog.repository.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.fixture.MemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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

  private Member member;

  @BeforeEach
  void setUp() {
    member = MemberFixture.createMember();
  }

  @Test
  @DisplayName("이메일로 회원 존재 여부를 확인할 수 있다")
  void existsByEmail_WhenMemberSaved_ThenReturnsTrue() {
    // given
    memberRepository.save(member);

    // when & then
    assertThat(memberRepository.existsByEmail(member.getEmail())).isTrue();
    assertThat(memberRepository.existsByEmail("other@email.com")).isFalse();
  }

  @Test
  @DisplayName("대소문자가 다른 이메일도 중복으로 처리된다")
  void save_WhenEmailDiffersByCase_ThenThrowsException() {
    // given
    memberRepository.save(member);

    Member another = MemberFixture.createMember("LUCKY@email.com");

    // when & then
    assertThatThrownBy(() -> {
      memberRepository.saveAndFlush(another);
    }).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("중복된 이메일로 저장 시 예외가 발생한다")
  void save_WhenEmailDuplicated_ThenThrowsException() {
    // given
    memberRepository.save(member);
    Member duplicate = MemberFixture.createMember(member.getEmail(), "other_nickname");

    // when & then
    assertThatThrownBy(() -> {
      memberRepository.saveAndFlush(duplicate);
    }).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("닉네임으로 회원 존재 여부를 확인할 수 있다")
  void existsByNickname_WhenMemberSaved_ThenReturnsTrue() {
    // given
    memberRepository.save(member);

    // when & then
    assertThat(memberRepository.existsByNickname(member.getNickname())).isTrue();
    assertThat(memberRepository.existsByNickname("other_nickname")).isFalse();
  }

  @Test
  @DisplayName("닉네임이 null인 회원을 저장할 수 있다")
  void save_WhenNicknameNull_ThenSavesSuccessfully() {
    // given
    Member member = MemberFixture.createMember("lucky@email.com", null);

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
    memberRepository.save(member);
    Member duplicate = MemberFixture.createMember("other@email.com", member.getNickname());

    // when & then
    assertThatThrownBy(() -> {
      memberRepository.saveAndFlush(duplicate);
    }).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("이메일로 회원 조회 시 회원을 반환한다")
  void findByEmail_WhenMemberExists_ThenReturnsMember() {
    // given
    memberRepository.save(member);

    // when
    Optional<Member> result = memberRepository.findByEmail(member.getEmail());

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo(member.getEmail());
    assertThat(result.get().getNickname()).isEqualTo(member.getNickname());
  }

  @Test
  @DisplayName("존재하지 않는 이메일로 조회 시 빈 Optional을 반환한다")
  void findByEmail_WhenMemberNotExists_ThenReturnsEmpty() {
    // when
    Optional<Member> result = memberRepository.findByEmail("lucky@email.com");

    // then
    assertThat(result).isEmpty();
  }
}
