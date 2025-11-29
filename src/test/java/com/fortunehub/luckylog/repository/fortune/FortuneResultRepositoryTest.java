package com.fortunehub.luckylog.repository.fortune;

import static org.assertj.core.api.Assertions.assertThat;

import com.fortunehub.luckylog.domain.fortune.FortuneResult;
import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.fixture.FortuneResultFixture;
import com.fortunehub.luckylog.fixture.MemberFixture;
import com.fortunehub.luckylog.repository.member.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql("/test-data/fortune-categories.sql")
class FortuneResultRepositoryTest {

  @Autowired
  private FortuneResultRepository fortuneResultRepository;

  @Autowired
  private MemberRepository memberRepository;

  private FortuneResult result;
  private Member member;

  @BeforeEach
  void setup() {
    member = memberRepository.save(MemberFixture.createMember());
    result = FortuneResultFixture.createFortuneResult(member);
  }

  @Test
  @DisplayName("id로 조회 시 운세 결과를 반환한다")
  void findByIdAndMember_IdAndIsActiveTrue_WhenFortuneResultExists_ThenReturnsFortuneResult() {
    // given
    FortuneResult saved = fortuneResultRepository.save(result);

    // when
    Optional<FortuneResult> found = fortuneResultRepository
        .findByIdAndMember_IdAndIsActiveTrue(saved.getId(), member.getId());

    // then
    assertThat(found).isPresent();
    assertThat(found.get())
        .extracting("id", "isActive")
        .containsExactly(saved.getId(), true);
  }

  @Test
  @DisplayName("존재하지 않는 id로 조회 시 빈 Optional을 반환한다")
  void findByIdAndMember_IdAndIsActiveTrue_WhenNotExists_ThenReturnsEmpty() {
    // when
    Optional<FortuneResult> found = fortuneResultRepository
        .findByIdAndMember_IdAndIsActiveTrue(999L, member.getId());

    // then
    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("다른 회원의 조회 시 빈 Optional을 반환한다")
  void findByIdAndMember_IdAndIsActiveTrue_WhenOtherMemberFortune_ThenReturnsEmpty() {
    // given
    FortuneResult saved = fortuneResultRepository.save(result);
    Long otherMemberId = 10L;

    // when
    Optional<FortuneResult> found = fortuneResultRepository
        .findByIdAndMember_IdAndIsActiveTrue(saved.getId(), otherMemberId);

    // then
    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("isActive가 false인 경우 조회되지 않는다")
  void findByIdAndMember_IdAndIsActiveTrue_WhenInactive_ReturnsEmpty() {
    // given
    result.softDelete();
    FortuneResult saved = fortuneResultRepository.save(result);

    // when
    Optional<FortuneResult> found = fortuneResultRepository
        .findByIdAndMember_IdAndIsActiveTrue(saved.getId(), member.getId());

    // then
    assertThat(found).isEmpty();
  }
}