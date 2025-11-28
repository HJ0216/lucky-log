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
import com.fortunehub.luckylog.dto.response.fortune.MyFortuneResponse;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import com.fortunehub.luckylog.fixture.FortuneResultFixture;
import com.fortunehub.luckylog.fixture.MemberFixture;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("운세 Service")
class FortuneServiceTest {

  @Mock
  private FortuneResultRepository fortuneResultRepository;

  @Mock
  private FortuneCategoryRepository fortuneCategoryRepository;

  @InjectMocks
  private FortuneService fortuneService;

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

  @BeforeEach
  void setUp() {
    member = MemberFixture.createMember();
    fortuneTypes = List.of(FortuneType.LOVE, FortuneType.HEALTH);
    request = createValidFortuneRequest(fortuneTypes);
  }

  @Test
  @DisplayName("정상적인 운세 저장 요청 시 운세가 저장된다")
  void save_WhenValidRequest_ThenSavesFortune() {
    // given
    given(fortuneResultRepository.existsByMember_IdAndTitle(member.getId(), TEST_TITLE))
        .willReturn(false);
    given(fortuneResultRepository.countByMember_IdAndIsActiveTrue(member.getId()))
        .willReturn(4L);

    given(fortuneCategoryRepository
        .findByFortuneTypeIn(any())).willReturn(
        getCategoriesByTypes(FortuneType.LOVE, FortuneType.HEALTH));

    // when
    fortuneService.save(member, request);

    // then
    verify(fortuneResultRepository).save(any(FortuneResult.class));
  }

  @Test
  @DisplayName("null 회원으로 저장 시 예외가 발생한다")
  void save_WhenMemberIsNull_ThenThrowsException() {
    // when
    assertThatThrownBy(() -> fortuneService.save(null, request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.INVALID_MEMBER.getMessage());

    verify(fortuneResultRepository, never()).save(any(FortuneResult.class));
  }

  @Test
  @DisplayName("비활성화된 회원으로 저장 시 예외가 발생한다")
  void save_WhenMemberIsInactive_ThenThrowsException() {
    // given
    Member member = MemberFixture.createInactiveMember();

    // when
    assertThatThrownBy(() -> fortuneService.save(member, request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.INVALID_MEMBER.getMessage());

    verify(fortuneResultRepository, never()).save(any(FortuneResult.class));
  }

  @Test
  @DisplayName("중복된 제목으로 저장 시 예외가 발생한다")
  void save_WhenTitleIsDuplicate_ThenThrowsException() {
    // given
    given(fortuneResultRepository.existsByMember_IdAndTitle(member.getId(), TEST_TITLE))
        .willReturn(true);

    // when & then
    assertThatThrownBy(() -> fortuneService.save(member, request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.DUPLICATE_FORTUNE_TITLE.getMessage());

    verify(fortuneResultRepository, never()).save(any(FortuneResult.class));
  }

  @Test
  @DisplayName("최대 저장 개수 초과 시 예외가 발생한다")
  void save_WhenExceedMaxSaveCount_ThenThrowsException() {
    // given
    given(fortuneResultRepository.existsByMember_IdAndTitle(member.getId(), TEST_TITLE))
        .willReturn(false);
    given(fortuneResultRepository.countByMember_IdAndIsActiveTrue(member.getId()))
        .willReturn(5L); // MAX_SAVE_COUNT만큼 운세가 저장됨

    // when & then
    assertThatThrownBy(() -> fortuneService.save(member, request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.EXCEED_MAX_SAVE_COUNT.getMessage());

    verify(fortuneResultRepository, never()).save(any(FortuneResult.class));
  }

  @Test
  @DisplayName("운세 카테고리를 찾을 수 없을 때 예외가 발생한다")
  void save_WhenCategoryNotFound_ThenThrowsException() {
    // given
    given(fortuneResultRepository.existsByMember_IdAndTitle(member.getId(), TEST_TITLE))
        .willReturn(false);
    given(fortuneResultRepository.countByMember_IdAndIsActiveTrue(member.getId()))
        .willReturn(4L);

    // 카테고리 1개만 반환 (2개 요청)
    List<FortuneCategory> categories = List.of(
        FortuneCategory.create(1, FortuneType.LOVE)
        // HEALTH 없음 → 예외 발생 예상
    );

    given(fortuneCategoryRepository.findByFortuneTypeIn(fortuneTypes))
        .willReturn(categories);

    // when & then
    assertThatThrownBy(() -> fortuneService.save(member, request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.FORTUNE_CATEGORY_NOT_FOUND.getMessage());

    verify(fortuneResultRepository, never()).save(any(FortuneResult.class));
  }

  @Test
  @DisplayName("저장된 운세 객체의 내용이 올바르게 설정된다")
  void save_WhenValidRequest_ThenSavesCorrectFortuneData() {
    // given
    given(fortuneResultRepository.existsByMember_IdAndTitle(member.getId(), TEST_TITLE))
        .willReturn(false);
    given(fortuneResultRepository.countByMember_IdAndIsActiveTrue(member.getId()))
        .willReturn(4L);

    given(fortuneCategoryRepository.findByFortuneTypeIn(fortuneTypes))
        .willReturn(getCategoriesByTypes(FortuneType.LOVE, FortuneType.HEALTH));

    // when
    fortuneService.save(member, request);

    // then
    ArgumentCaptor<FortuneResult> captor = ArgumentCaptor.forClass(FortuneResult.class);
    verify(fortuneResultRepository).save(captor.capture());

    FortuneResult savedResult = captor.getValue();
    assertThat(savedResult.getTitle()).isEqualTo(TEST_TITLE);
    assertThat(savedResult.getMember()).isEqualTo(member);
    assertThat(savedResult.getItems()).hasSize(4);
    assertThat(savedResult.getCategories()).hasSize(2);
    assertThat(savedResult.getItems())
        .extracting(FortuneResultItem::getContent)
        .containsExactlyInAnyOrder("좋은 한 해가 될 것입니다.", "건강운이 상승합니다.",
            "건강 유지를 위해 운동이 필요합니다.", "좋은 인연을 만나게 될 것입니다.");
  }

  @Test
  @DisplayName("정상적인 운세 목록 요청 시 조회된다")
  void getMyFortunes_WhenValidRequest_ThenReturnsMyFortunes() {
    // given
    Member member = MemberFixture.createMemberWithId();
    List<FortuneResult> results = FortuneResultFixture.createFortuneResults(member, 3);

    given(fortuneResultRepository.findAllByMember_IdAndIsActiveTrue(member.getId()))
        .willReturn(results);

    // when
    List<MyFortuneResponse> result = fortuneService.getMyFortunes(member.getId());

    // then
    verify(fortuneResultRepository).findAllByMember_IdAndIsActiveTrue(member.getId());

    MyFortuneResponse firstFortune = result.get(0);
    assertThat(firstFortune.getTitle()).isEqualTo("2025년 월별 운세 1");
    assertThat(firstFortune.getFortuneTypeDisplayName()).isNotBlank();
    assertThat(firstFortune.getCreatedAt()).isNotBlank();

    assertThat(result)
        .extracting(MyFortuneResponse::getTitle)
        .containsExactly(
            "2025년 월별 운세 1",
            "2025년 월별 운세 2",
            "2025년 월별 운세 3"
        );
  }

  @Test
  @DisplayName("저장된 운세가 없으면 빈 리스트를 반환한다")
  void getMyFortunes_WhenNoFortunes_ThenReturnsEmptyList() {
    // given
    Member member = MemberFixture.createMemberWithId();
    given(fortuneResultRepository.findAllByMember_IdAndIsActiveTrue(member.getId()))
        .willReturn(List.of());

    // when
    List<MyFortuneResponse> result = fortuneService.getMyFortunes(member.getId());

    // then
    assertThat(result).isEmpty();
  }

  private SaveFortuneRequest createValidFortuneRequest(List<FortuneType> fortunes) {
    FortuneOptionForm option = createValidFortuneOption(fortunes);
    List<FortuneResponse> responses = createValidFortuneResponses();

    SaveFortuneRequest request = new SaveFortuneRequest();
    request.setTitle(TEST_TITLE);
    request.setBirthInfo(createValidBirthInfo());
    request.setOption(option);
    request.setFortuneResultYear(2025);
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
    response1.setFortune(FortuneType.LOVE);
    response1.setPeriodValue(PeriodValue.JANUARY);
    response1.setResult("좋은 한 해가 될 것입니다.");

    FortuneResponse response2 = new FortuneResponse();
    response2.setFortune(FortuneType.HEALTH);
    response2.setPeriodValue(PeriodValue.FEBRUARY);
    response2.setResult("건강운이 상승합니다.");

    FortuneResponse response3 = new FortuneResponse();
    response3.setFortune(FortuneType.HEALTH);
    response3.setPeriodValue(PeriodValue.MARCH);
    response3.setResult("건강 유지를 위해 운동이 필요합니다.");

    FortuneResponse response4 = new FortuneResponse();
    response4.setFortune(FortuneType.LOVE);
    response4.setPeriodValue(PeriodValue.APRIL);
    response4.setResult("좋은 인연을 만나게 될 것입니다.");

    return List.of(response1, response2, response3, response4);
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