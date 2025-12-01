package com.fortunehub.luckylog.repository.fortune;

import static org.assertj.core.api.Assertions.assertThat;

import com.fortunehub.luckylog.domain.fortune.FortuneResult;
import com.fortunehub.luckylog.domain.fortune.FortuneResultItem;
import com.fortunehub.luckylog.domain.fortune.PeriodValue;
import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.fixture.FortuneResultFixture;
import com.fortunehub.luckylog.fixture.MemberFixture;
import com.fortunehub.luckylog.repository.member.MemberRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
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

  @Autowired
  private EntityManager entityManager;

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

  // EntityGraph가 실제로 N+1 문제를 방지하는지 검증하는 테스트
  @Test
  @DisplayName("EntityGraph로 items와 categories를 함께 조회한다 (N+1 없음)")
  void findByIdAndMember_IdAndIsActiveTrue_FetchesItemsAndCategories() {
    // given
    FortuneResult saved = fortuneResultRepository.save(result);
    fortuneResultRepository.flush(); // DB에 실제로 저장

    entityManager.clear();
    // repository.save 시, 영속성 컨텍스트(JPA가 엔티티를 관리하는 환경)에 캐시됨
    // repository.findById 시, DB 조회 안 하고 1차 캐시(영속성 컨텍스트 내부의 캐시 저장소)에서 가져옴

    // when
    Optional<FortuneResult> found = fortuneResultRepository
        .findByIdAndMember_IdAndIsActiveTrue(saved.getId(), member.getId());
    // 실제로 DB 조회 쿼리 실행

    // then
    assertThat(found).isPresent();
    FortuneResult foundResult = found.get();

    // 지연 로딩 없이 접근 가능
    assertThat(foundResult.getItems()).isNotEmpty();
    assertThat(foundResult.getCategories()).isNotEmpty();

    // 2단계 fetch 확인
    foundResult.getCategories().forEach(category -> {
      assertThat(category.getFortuneCategory()).isNotNull();
      assertThat(category.getFortuneCategory().getFortuneType()).isNotNull();
    });
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

  @Test
  @DisplayName("LinkedHashSet으로 월별 운세 순서가 보장된다")
  void findByIdAndMember_IdAndIsActiveTrue_WhenlinkedHashSet_ReturnsOrder() {
    // given
    FortuneResult result = FortuneResultFixture.createFortuneResult(member);
    FortuneResult saved = fortuneResultRepository.save(result);

    entityManager.flush();
    entityManager.clear();

    // when
    Optional<FortuneResult> foundOptional = fortuneResultRepository
        .findByIdAndMember_IdAndIsActiveTrue(saved.getId(), member.getId());

    FortuneResult found = foundOptional.get();

    List<PeriodValue> periodValues = found.getItems().stream()
                                          .map(FortuneResultItem::getPeriodValue)
                                          .toList();

    assertThat(foundOptional).isPresent();
    assertThat(periodValues).containsExactly(
        PeriodValue.JANUARY,
        PeriodValue.FEBRUARY,
        PeriodValue.MARCH,
        PeriodValue.APRIL
    );
  }
}