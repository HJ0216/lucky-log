package com.fortunehub.luckylog.client.gemini;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.domain.fortune.AIType;
import com.fortunehub.luckylog.domain.fortune.CalendarType;
import com.fortunehub.luckylog.domain.fortune.CityType;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.fortune.GenderType;
import com.fortunehub.luckylog.domain.fortune.PeriodType;
import com.fortunehub.luckylog.domain.fortune.PeriodValue;
import com.fortunehub.luckylog.domain.fortune.TimeType;
import com.fortunehub.luckylog.dto.request.fortune.FortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import com.google.genai.Client;
import com.google.genai.Models;
import com.google.genai.errors.ServerException;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GeminiServiceTest {

  @Mock
  private Client client;
  @Mock
  private Models models;
  @Mock // Mock 객체 생성
  private GenerateContentConfig generateContentConfig;

  GeminiService service;

  private static final String MODEL_NAME = "gemini-test";

  private static final String VALID_JSON_RESPONSE = """
      [
        {
          "fortune": "love",
          "periodValue": "january",
          "result": "연애운 좋음"
        },
        {
          "fortune": "love",
          "periodValue": "february",
          "result": "연애운 신경"
        },
        {
          "fortune": "health",
          "periodValue": "march",
          "result": "건강운 변화"
        }
      ]
      """;

  // InjectMocks로는 @Value 필드는 주입 안 됨
  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(client, "models", models); // final field

    service = new GeminiService(
        client,
        generateContentConfig,
        new ObjectMapper(),
        "gemini-test",
        "[ANALYSIS_YEAR]년 [FORTUNE_TYPES] 운세 분석"
    );
  }

  @Test
  @DisplayName("정상적인 운세 요청 시 결과를 반환한다")
  void analyzeFortune_WhenValidRequest_ThenReturnsFortuneResponses() {
    // given
    GenerateContentResponse response = mock(GenerateContentResponse.class);

    given(client.models.generateContent(
        eq(MODEL_NAME),
        anyString(),
        eq(generateContentConfig))).willReturn(response);

    String rawResponse = "```json\n" + VALID_JSON_RESPONSE + "\n```";
    given(response.text()).willReturn(rawResponse);

    // private method인 parseFortuneResponse는 public method를 통해 간접 테스트

    // when
    FortuneRequest request = createFortuneRequest();
    List<FortuneResponse> responses = service.analyzeFortune(request);

    // then
    assertThat(responses)
        .isNotNull()
        .hasSize(3)
        .extracting(FortuneResponse::getFortune, FortuneResponse::getPeriodValue,
            FortuneResponse::getResult)
        .containsExactly(
            tuple(FortuneType.LOVE, PeriodValue.JANUARY, "연애운 좋음"),
            tuple(FortuneType.LOVE, PeriodValue.FEBRUARY, "연애운 신경"),
            tuple(FortuneType.HEALTH, PeriodValue.MARCH, "건강운 변화")
        );

    ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
    verify(client.models).generateContent(
        eq(MODEL_NAME),
        promptCaptor.capture(),
        eq(generateContentConfig)
    );

    String prompt = promptCaptor.getValue();
    assertThat(prompt)
        .contains("2025년")
        .contains("애정, 건강")
        .contains("1995년 2월 16일")
        .contains("양력")
        .contains("여성")
        .contains("오시")
        .contains("서울특별시");
  }

  @Test
  @DisplayName("빈 응답일 경우 예외가 발생한다")
  void analyzeFortune_WhenResponseEmpty_ThenThrowsException() {
    // given
    GenerateContentResponse response = mock(GenerateContentResponse.class);
    given(client.models.generateContent(
        eq(MODEL_NAME),
        anyString(),
        eq(generateContentConfig))).willReturn(response);

    given(response.text()).willReturn("  ");

    // when & then
    FortuneRequest request = createFortuneRequest();

    assertThatThrownBy(() -> service.analyzeFortune(request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.GEMINI_EMPTY_RESPONSE.getMessage());
  }

  @Test
  @DisplayName("응답이 없을 경우 예외가 발생한다")
  void analyzeFortune_WhenResponseNull_ThenThrowsException() {
    // given
    given(client.models.generateContent(
        eq(MODEL_NAME),
        anyString(),
        eq(generateContentConfig))).willReturn(null);

    // when & then
    FortuneRequest request = createFortuneRequest();

    assertThatThrownBy(() -> service.analyzeFortune(request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.GEMINI_UNKNOWN_ERROR.getMessage());
  }

  @Test
  @DisplayName("잘못된 형식의 JSON 응답일 경우 예외가 발생한다")
  void analyzeFortune_WhenInvalidResponse_ThenThrowsException() {
    // given
    GenerateContentResponse response = mock(GenerateContentResponse.class);
    given(client.models.generateContent(
        eq(MODEL_NAME),
        anyString(),
        eq(generateContentConfig))).willReturn(response);

    String rawResponse = "```json\ninvalid json\n```";
    given(response.text()).willReturn(rawResponse);

    // when & then
    FortuneRequest request = createFortuneRequest();

    assertThatThrownBy(() -> service.analyzeFortune(request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.GEMINI_RESPONSE_PARSE_ERROR.getMessage());
  }

  @Test
  @DisplayName("API 호출이 실패하면 예외가 발생한다")
  void analyzeFortune_WhenApiFails_ThenThrowsException() {
    // given
    given(client.models.generateContent(
        eq(MODEL_NAME),
        anyString(),
        eq(generateContentConfig))).willThrow(new ServerException(500, "ERROR", "API 서버 오류"));

    // when & then
    FortuneRequest request = createFortuneRequest();

    assertThatThrownBy(() -> service.analyzeFortune(request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.GEMINI_OVERLOAD.getMessage());

  }

  private FortuneRequest createFortuneRequest() {
    BirthInfoForm birthForm = createBirthInfoForm();
    FortuneOptionForm optionForm = createFortuneOptionForm();

    return FortuneRequest.from(birthForm, optionForm, 2025);
  }

  private BirthInfoForm createBirthInfoForm() {
    BirthInfoForm birthForm = new BirthInfoForm();
    birthForm.setGender(GenderType.FEMALE);
    birthForm.setCalendar(CalendarType.SOLAR);
    birthForm.setYear(1995);
    birthForm.setMonth(2);
    birthForm.setDay(16);
    birthForm.setTime(TimeType.TIME_11_30);
    birthForm.setCity(CityType.SEOUL);
    return birthForm;
  }

  private FortuneOptionForm createFortuneOptionForm() {
    FortuneOptionForm optionForm = new FortuneOptionForm();
    optionForm.setAi(AIType.GEMINI);
    optionForm.setFortunes(new ArrayList<>(List.of(FortuneType.LOVE, FortuneType.HEALTH)));
    optionForm.setPeriod(PeriodType.MONTHLY);
    return optionForm;
  }
}