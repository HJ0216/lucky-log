package com.fortunehub.luckylog.client.gemini;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.domain.fortune.AIType;
import com.fortunehub.luckylog.domain.fortune.CalendarType;
import com.fortunehub.luckylog.domain.fortune.CityType;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.fortune.GenderType;
import com.fortunehub.luckylog.domain.fortune.MonthType;
import com.fortunehub.luckylog.domain.fortune.PeriodType;
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
import org.mockito.InjectMocks;
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

  @InjectMocks // Mock 객체들을 자동으로 주입(service instance 생성 -> @Mock으로 만든 필드 주입)
  private GeminiService geminiService;

  private static final String MODEL_NAME = "gemini-test";
  private static final String PROMPT_TEMPLATE = "[ANALYSIS_YEAR]년 [FORTUNE_TYPES] 운세 분석";

  private static final String VALID_JSON_RESPONSE = """
      [
        {
          "fortune": "love",
          "month": "january",
          "result": "연애운 좋음"
        },
        {
          "fortune": "love",
          "month": "february",
          "result": "연애운 신경"
        },
        {
          "fortune": "health",
          "month": "march",
          "result": "건강운 변화"
        }
      ]
      """;

  // InjectMocks로는 @Value 필드는 주입 안 됨
  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(client, "models", models); // final field
    ReflectionTestUtils.setField(geminiService, "modelName", MODEL_NAME);
    ReflectionTestUtils.setField(geminiService, "promptTemplate", PROMPT_TEMPLATE);
    ReflectionTestUtils.setField(geminiService, "objectMapper", new ObjectMapper());
  }

  @Test
  @DisplayName("정상적인 운세 요청 시 결과를 반환한다")
  void analyzeFortune_WhenValidRequest_ThenReturnsFortuneResponses() {
    // given
    GenerateContentResponse response = mock(GenerateContentResponse.class);
    when(client.models.generateContent(
        eq(MODEL_NAME),
        anyString(),
        eq(generateContentConfig))).thenReturn(response);

    String rawResponse = "```json\n" + VALID_JSON_RESPONSE + "\n```";
    when(response.text()).thenReturn(rawResponse);

    // private method인 parseFortuneResponse는 public method를 통해 간접 테스트

    // when
    FortuneRequest request = createFortuneRequest();
    List<FortuneResponse> responses = geminiService.analyzeFortune(request);

    // then
    int expectedFortuneTypes = 3;
    assertThat(responses).isNotNull().hasSize(expectedFortuneTypes);

    assertThat(responses.get(0).getFortune()).isEqualTo(FortuneType.LOVE);
    assertThat(responses.get(0).getMonth()).isEqualTo(MonthType.JANUARY);
    assertThat(responses.get(0).getResult()).isEqualTo("연애운 좋음");

    assertThat(responses.get(1).getFortune()).isEqualTo(FortuneType.LOVE);
    assertThat(responses.get(1).getMonth()).isEqualTo(MonthType.FEBRUARY);
    assertThat(responses.get(1).getResult()).isEqualTo("연애운 신경");

    assertThat(responses.get(2).getFortune()).isEqualTo(FortuneType.HEALTH);
    assertThat(responses.get(2).getMonth()).isEqualTo(MonthType.MARCH);
    assertThat(responses.get(2).getResult()).isEqualTo("건강운 변화");

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
    when(client.models.generateContent(
        eq(MODEL_NAME),
        anyString(),
        eq(generateContentConfig))).thenReturn(response);

    when(response.text()).thenReturn("  ");

    // when & then
    FortuneRequest request = createFortuneRequest();

    assertThatThrownBy(() -> geminiService.analyzeFortune(request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.GEMINI_EMPTY_RESPONSE.getMessage());
  }

  @Test
  @DisplayName("응답이 없을 경우 예외가 발생한다")
  void analyzeFortune_WhenResponseNull_ThenThrowsException() {
    // given
    when(client.models.generateContent(
        eq(MODEL_NAME),
        anyString(),
        eq(generateContentConfig))).thenReturn(null);

    // when & then
    FortuneRequest request = createFortuneRequest();

    assertThatThrownBy(() -> geminiService.analyzeFortune(request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.GEMINI_UNKNOWN_ERROR.getMessage());
  }

  @Test
  @DisplayName("잘못된 형식의 JSON 응답일 경우 예외가 발생한다")
  void analyzeFortune_WhenInvalidResponse_ThenThrowsException() {
    // given
    GenerateContentResponse response = mock(GenerateContentResponse.class);
    when(client.models.generateContent(
        eq(MODEL_NAME),
        anyString(),
        eq(generateContentConfig))).thenReturn(response);

    String rawResponse = "```json\ninvalid json\n```";
    when(response.text()).thenReturn(rawResponse);

    // when & then
    FortuneRequest request = createFortuneRequest();

    assertThatThrownBy(() -> geminiService.analyzeFortune(request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.GEMINI_RESPONSE_PARSE_ERROR.getMessage());
  }

  @Test
  @DisplayName("API 호출이 실패하면 예외가 발생한다")
  void analyzeFortune_WhenApiFails_ThenThrowsException() {
    // given
    when(client.models.generateContent(
        eq(MODEL_NAME),
        anyString(),
        eq(generateContentConfig))).thenThrow(new ServerException(500, "ERROR", "API 서버 오류"));

    // when & then
    FortuneRequest request = createFortuneRequest();

    assertThatThrownBy(() -> geminiService.analyzeFortune(request))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(ErrorCode.GEMINI_OVERLOAD.getMessage());

  }

  private FortuneRequest createFortuneRequest() {
    BirthInfoForm birthForm = createBirthInfoForm();
    FortuneOptionForm optionForm = createFortuneOptionForm();

    return FortuneRequest.from(birthForm, optionForm);
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