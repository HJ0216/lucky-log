package com.fortunehub.luckylog.domain.fortune;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.dto.request.fortune.SaveFortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("FortuneResult Entity")
class FortuneResultTest {

  private Member member;
  private SaveFortuneRequest request;
  private BirthInfoForm birthInfo;

  @BeforeEach
  void setUp() {
    member = new Member("test@email.com", "password123", "테스터");
    request = createValidRequest();
    birthInfo = createValidBirthInfo();
  }

  @Test
  @DisplayName("null 회원으로 생성 시 예외가 발생한다")
  void create_WhenMemberIsNull_ThenThrowsException() {
    // when & then
    assertThatThrownBy(() -> FortuneResult.create(null, request, birthInfo))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.MEMBER_INFO_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("null 요청으로 생성 시 예외가 발생한다")
  void create_WhenRequestIsNull_ThenThrowsException() {
    // when & then
    assertThatThrownBy(() -> FortuneResult.create(member, null, birthInfo))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.FORTUNE_REQUEST_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("운세 결과 연도가 null이면 예외가 발생한다")
  void create_WhenFortuneResultYearIsNull_ThenThrowsException() {
    // given
    request.setFortuneResultYear(null);

    // when & then
    assertThatThrownBy(() -> FortuneResult.create(member, request, birthInfo))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.FORTUNE_YEAR_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("운세 옵션 정보가 null이면 예외가 발생한다")
  void create_WhenFortuneOptionIsNull_ThenThrowsException() {
    // given
    request.setOption(null);

    // when & then
    assertThatThrownBy(() -> FortuneResult.create(member, request, birthInfo))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.FORTUNE_OPTION_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("운세 결과가 null이면 예외가 발생한다")
  void create_WhenResponsesIsNull_ThenThrowsException() {
    // given
    request.setResponses(null);

    // when & then
    assertThatThrownBy(() -> FortuneResult.create(member, request, birthInfo))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.FORTUNE_RESPONSE_REQUIRED.getMessage());
  }
  
  @Test
  @DisplayName("운세 결과가 없으면 예외가 발생한다")
  void create_WhenResponsesIsEmpty_ThenThrowsException() {
    // given
    request.setResponses(List.of());

    // when & then
    assertThatThrownBy(() -> FortuneResult.create(member, request, birthInfo))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.FORTUNE_RESPONSE_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("null 생년월일로 생성 시 예외가 발생한다")
  void create_WhenBirthInfoIsNull_ThenThrowsException() {
    // when & then
    assertThatThrownBy(() -> FortuneResult.create(member, request, null))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.BIRTH_INFO_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("생년월일의 년도가 null이면 예외가 발생한다")
  void create_WhenBirthYearIsNull_ThenThrowsException() {
    // given
    birthInfo.setYear(null);

    // when & then
    assertThatThrownBy(() -> FortuneResult.create(member, request, birthInfo))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.BIRTH_DATE_REQUIRED.getMessage());
  }


  @Test
  @DisplayName("생년월일의 일이 null이면 예외가 발생한다")
  void create_WhenBirthDayIsNull_ThenThrowsException() {
    // given
    birthInfo.setDay(null);

    // when & then
    assertThatThrownBy(() -> FortuneResult.create(member, request, birthInfo))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.BIRTH_DATE_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("제목이 없으면 자동으로 생성한다")
  void create_WhenTitleIsEmpty_ThenGeneratesTitle() {
    // given
    request.setTitle("");

    // when
    FortuneResult result = FortuneResult.create(member, request, birthInfo);

    // then
    assertThat(result.getTitle()).isNotBlank();
    assertThat(result.getTitle()).contains(String.valueOf(request.getFortuneResultYear()));
    assertThat(result.getTitle()).contains(request.getOption().getPeriod().getDisplayName());
    assertThat(result.getTitle()).contains(request.getOption().getFortunesAsString());
  }

  @Test
  @DisplayName("제목이 null이면 자동으로 생성한다")
  void create_WhenTitleIsNull_ThenGeneratesTitle() {
    // given
    request.setTitle(null);

    // when
    FortuneResult result = FortuneResult.create(member, request, birthInfo);

    // then
    assertThat(result.getTitle()).isNotBlank();
    assertThat(result.getTitle()).contains(String.valueOf(request.getFortuneResultYear()));
    assertThat(result.getTitle()).contains(request.getOption().getPeriod().getDisplayName());
    assertThat(result.getTitle()).contains(request.getOption().getFortunesAsString());
  }

  @Test
  @DisplayName("유효하지 않은 날짜로 생성 시 예외가 발생한다")
  void create_WhenInvalidDate_ThenThrowsException() {
    // given
    birthInfo.setYear(2024);
    birthInfo.setMonth(2);
    birthInfo.setDay(30);  // 2월 30일은 존재하지 않음

    // when & then
    assertThatThrownBy(() -> FortuneResult.create(member, request, birthInfo))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.INVALID_BIRTH_DATE.getMessage());
  }

  private SaveFortuneRequest createValidRequest() {
    SaveFortuneRequest req = new SaveFortuneRequest();
    req.setTitle("2025년 운세");
    req.setFortuneResultYear(2025);

    FortuneOptionForm option = new FortuneOptionForm();
    option.setAi(AIType.GEMINI);
    option.setFortunes(List.of(FortuneType.OVERALL));
    option.setPeriod(PeriodType.MONTHLY);
    req.setOption(option);

    FortuneResponse response = new FortuneResponse();
    response.setPeriodValue(PeriodValue.JANUARY);
    response.setResult("좋은 운세");
    req.setResponses(List.of(response));

    return req;
  }

  private BirthInfoForm createValidBirthInfo() {
    BirthInfoForm form = new BirthInfoForm();
    form.setGender(GenderType.FEMALE);
    form.setCalendar(CalendarType.SOLAR);
    form.setYear(1995);
    form.setMonth(2);
    form.setDay(16);
    form.setCity(CityType.SEOUL);
    form.setTime(TimeType.TIME_11_30);
    return form;
  }
}