package com.fortunehub.luckylog.service.fortune;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.domain.fortune.AIType;
import com.fortunehub.luckylog.domain.fortune.CalendarType;
import com.fortunehub.luckylog.domain.fortune.CityType;
import com.fortunehub.luckylog.domain.fortune.FortuneCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneResult;
import com.fortunehub.luckylog.domain.fortune.FortuneResultItem;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.fortune.GenderType;
import com.fortunehub.luckylog.domain.fortune.PeriodType;
import com.fortunehub.luckylog.domain.fortune.PeriodValue;
import com.fortunehub.luckylog.domain.fortune.TimeType;
import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.dto.request.fortune.SaveFortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import com.fortunehub.luckylog.repository.fortune.FortuneCategoryRepository;
import com.fortunehub.luckylog.repository.fortune.FortuneResultRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("운세 Service")
class FortuneServiceTest {

  @Mock
  private FortuneResultRepository fortuneResultRepository;

  @Mock
  private FortuneCategoryRepository fortuneCategoryRepository;

  @InjectMocks
  private FortuneService fortuneService;

  private static final Long TEST_MEMBER_ID = 1L;
  private static final String TEST_TITLE = "2025년 운세";

  private static final List<FortuneCategory> ALL_CATEGORIES = List.of(
      FortuneCategory.create(1, FortuneType.OVERALL),
      FortuneCategory.create(2, FortuneType.MONEY),
      FortuneCategory.create(3, FortuneType.LOVE),
      FortuneCategory.create(4, FortuneType.CAREER),
      FortuneCategory.create(5, FortuneType.STUDY),
      FortuneCategory.create(6, FortuneType.LUCK),
      FortuneCategory.create(7, FortuneType.FAMILY),
      FortuneCategory.create(8, FortuneType.HEALTH)
  );

  private Member member;
  private List<FortuneType> fortuneTypes;
  private SaveFortuneRequest request;
  private BirthInfoForm birthInfo;

  @BeforeEach
  void setUp() {
    member = createMemberWithId();
    fortuneTypes = List.of(FortuneType.OVERALL, FortuneType.MONEY);
    request = createValidFortuneRequest(fortuneTypes);
    birthInfo = createValidBirthInfo();
  }

  @Test
  @DisplayName("정상적인 운세 저장 요청 시 운세가 저장된다")
  void save_WhenValidRequest_ThenSavesFortune() {
    // given
    given(fortuneResultRepository.existsByMember_IdAndTitle(member.getId(), TEST_TITLE))
        .willReturn(false);
    given(fortuneResultRepository.countByMember_IdAndIsActiveTrue(TEST_MEMBER_ID))
        .willReturn(5L);

    given(fortuneCategoryRepository
        .findByFortuneTypeIn(any())).willReturn(getCategoriesByTypes(FortuneType.OVERALL, FortuneType.MONEY));

    // when
    fortuneService.save(member, request, birthInfo);

    // then
    verify(fortuneResultRepository).save(any(FortuneResult.class));
  }

  @Test
  @DisplayName("null 회원으로 저장 시 예외가 발생한다")
  void save_WhenMemberIsNull_ThenThrowsException() {
    // when
    assertThatThrownBy(() -> fortuneService.save(null, request, birthInfo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("유효하지 않은 회원입니다.");

    verify(fortuneResultRepository, never()).save(any(FortuneResult.class));
  }

  @Test
  @DisplayName("비활성화된 회원으로 저장 시 예외가 발생한다")
  void save_WhenMemberIsInactive_ThenThrowsException() {
    // given
    Member member = createMemberWithInactive();

    // when
    assertThatThrownBy(() -> fortuneService.save(member, request, birthInfo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("유효하지 않은 회원입니다.");

    verify(fortuneResultRepository, never()).save(any(FortuneResult.class));
  }

  @Test
  @DisplayName("중복된 제목으로 저장 시 예외가 발생한다")
  void save_WhenTitleIsDuplicate_ThenThrowsException() {
    // given
    given(fortuneResultRepository.existsByMember_IdAndTitle(member.getId(), TEST_TITLE))
        .willReturn(true);

    // when & then
    assertThatThrownBy(() -> fortuneService.save(member, request, birthInfo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이미 동일한 이름의 운세가 저장되어 있습니다.");

    verify(fortuneResultRepository, never()).save(any(FortuneResult.class));
  }

  @Test
  @DisplayName("최대 저장 개수 초과 시 예외가 발생한다")
  void save_WhenExceedMaxSaveCount_ThenThrowsException() {
    // given
    given(fortuneResultRepository.existsByMember_IdAndTitle(TEST_MEMBER_ID, TEST_TITLE))
        .willReturn(false);
    given(fortuneResultRepository.countByMember_IdAndIsActiveTrue(TEST_MEMBER_ID))
        .willReturn(11L); // MAX_SAVE_COUNT보다 큼

    // when & then
    assertThatThrownBy(() -> fortuneService.save(member, request, birthInfo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("저장 가능한 운세 개수를 초과했습니다.");

    verify(fortuneResultRepository, never()).save(any(FortuneResult.class));
  }

  @Test
  @DisplayName("운세 카테고리를 찾을 수 없을 때 예외가 발생한다")
  void save_WhenCategoryNotFound_ThenThrowsException() {
    // given
    given(fortuneResultRepository.existsByMember_IdAndTitle(TEST_MEMBER_ID, TEST_TITLE))
        .willReturn(false);
    given(fortuneResultRepository.countByMember_IdAndIsActiveTrue(TEST_MEMBER_ID))
        .willReturn(5L);

    // 카테고리 1개만 반환 (2개 요청)
    List<FortuneCategory> categories = List.of(
        FortuneCategory.create(1, FortuneType.OVERALL)
        // MONEY 없음 → 예외 발생 예상
    );

    given(fortuneCategoryRepository.findByFortuneTypeIn(fortuneTypes))
        .willReturn(categories);

    // when & then
    assertThatThrownBy(() -> fortuneService.save(member, request, birthInfo))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("일부 운세 카테고리를 찾을 수 없습니다.");

    verify(fortuneResultRepository, never()).save(any(FortuneResult.class));
  }

  @Test
  @DisplayName("저장된 운세 객체의 내용이 올바르게 설정된다")
  void save_WhenValidRequest_ThenSavesCorrectFortuneData() {
    // given
    given(fortuneResultRepository.existsByMember_IdAndTitle(TEST_MEMBER_ID, TEST_TITLE))
        .willReturn(false);
    given(fortuneResultRepository.countByMember_IdAndIsActiveTrue(TEST_MEMBER_ID))
        .willReturn(5L);

    given(fortuneCategoryRepository.findByFortuneTypeIn(fortuneTypes))
        .willReturn(getCategoriesByTypes(FortuneType.OVERALL, FortuneType.MONEY));

    // when
    fortuneService.save(member, request, birthInfo);

    // then
    ArgumentCaptor<FortuneResult> captor = ArgumentCaptor.forClass(FortuneResult.class);
    verify(fortuneResultRepository).save(captor.capture());

    FortuneResult savedResult = captor.getValue();
    assertThat(savedResult.getTitle()).isEqualTo(TEST_TITLE);
    assertThat(savedResult.getMember()).isEqualTo(member);
    assertThat(savedResult.getItems()).hasSize(2);
    assertThat(savedResult.getCategories()).hasSize(2);
    assertThat(savedResult.getItems())
        .extracting(FortuneResultItem::getContent)
        .containsExactly("좋은 한 해가 될 것입니다.", "재물운이 상승합니다.");
  }

  private Member createMemberWithId() {
    Member member = new Member("test@email.com", "encodedPassword", "솜사탕 구름");
    ReflectionTestUtils.setField(member, "id", TEST_MEMBER_ID);
    return member;
  }

  private Member createMemberWithInactive() {
    Member member = new Member("test@email.com", "encodedPassword", "솜사탕 구름");
    ReflectionTestUtils.setField(member, "isActive", false);
    return member;
  }

  private SaveFortuneRequest createValidFortuneRequest(List<FortuneType> fortunes) {
    FortuneOptionForm option = createValidFortuneOption(fortunes);
    List<FortuneResponse> responses = createValidFortuneResponses();

    SaveFortuneRequest request = new SaveFortuneRequest();
    request.setTitle(TEST_TITLE);
    request.setFortuneResultYear(2025);
    request.setOption(option);
    request.setResponses(responses);

    return request;
  }

  private FortuneOptionForm createValidFortuneOption(List<FortuneType> fortunes) {
    FortuneOptionForm option = new FortuneOptionForm();
    option.setAi(AIType.GEMINI);
    option.setFortunes(fortunes);
    option.setPeriod(PeriodType.MONTHLY);
    return option;
  }

  private List<FortuneResponse> createValidFortuneResponses() {
    FortuneResponse response1 = new FortuneResponse();
    response1.setFortune(FortuneType.OVERALL);
    response1.setPeriodValue(PeriodValue.JANUARY);
    response1.setResult("좋은 한 해가 될 것입니다.");

    FortuneResponse response2 = new FortuneResponse();
    response2.setFortune(FortuneType.MONEY);
    response2.setPeriodValue(PeriodValue.FEBRUARY);
    response2.setResult("재물운이 상승합니다.");
    return List.of(response1, response2);
  }

  private BirthInfoForm createValidBirthInfo() {
    BirthInfoForm birth = new BirthInfoForm();
    birth.setGender(GenderType.FEMALE);
    birth.setCalendar(CalendarType.SOLAR);
    birth.setYear(1995);
    birth.setMonth(2);
    birth.setDay(16);
    birth.setCity(CityType.SEOUL);
    birth.setTime(TimeType.TIME_11_30);
    return birth;
  }

  private List<FortuneCategory> getCategoriesByTypes(FortuneType... types) {
    return ALL_CATEGORIES.stream()
                         .filter(c -> Arrays.asList(types).contains(c.getFortuneType()))
                         .toList();
  }
}