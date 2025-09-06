package com.fortunehub.luckylog.service.fortune;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponseView;
import com.google.genai.Client;
import com.google.genai.Models;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GeminiServiceTest {

  @InjectMocks // Mock 객체들을 자동으로 주입(service instance 생성 -> @Mock으로 만든 필드 주입)
  private GeminiService geminiService;
  @Mock
  private Client client;
  @Mock
  private Models models;
  @Mock // Mock 객체 생성
  private GenerateContentConfig generateContentConfig;
  @Mock
  private ObjectMapper objectMapper;

  private static final String VALID_JSON_RESPONSE = """
      [
        {
          "fortune": "love",
          "month": "january",
          "result": ""
        },
        {
          "fortune": "love",
          "month": "february",
          "result": ""
        },
        {
          "fortune": "health",
          "month": "january",
          "result": ""
        }
      ]
      """;

  // InjectMocks로는 @Value 필드는 주입 안 됨
  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(client, "models", models); // final field
    ReflectionTestUtils.setField(geminiService, "modelName", "gemini-test");
    ReflectionTestUtils.setField(geminiService, "promptTemplate",
        "[ANALYSIS_YEAR]년 [FORTUNE_TYPES] 운세 분석");
  }

  @Nested
  @DisplayName("analyzeFortune()는")
  class Describe_analyzeFortune {

    @Nested
    @DisplayName("유효한 운세 요청이면")
    class Context_with_valid_request {

      @Test
      @DisplayName("운세 결과를 반환한다")
      void it_returns_fortune_result_response() throws JsonProcessingException {
        // given(setup mocks)
        GenerateContentResponse mockResponse = mock(GenerateContentResponse.class);

        when(client.models.generateContent(
            eq("gemini-test"),
            anyString(),
            eq(generateContentConfig))
        )
            .thenReturn(mockResponse);

        String jsonWithMarkdown = "```json\n" + VALID_JSON_RESPONSE + "\n```";
        when(mockResponse.text()).thenReturn(jsonWithMarkdown);

        List<FortuneResponse> fortuneResponses = createFortuneResponses();
        when(objectMapper.readValue(
            anyString(),
            ArgumentMatchers.any(TypeReference.class)))
            .thenReturn(fortuneResponses);

        // when
        FortuneRequest request = createFortuneRequest();
        List<FortuneResponseView> views = geminiService.analyzeFortune(request);

        // then
        assertThat(views).isNotNull().hasSize(2);
        assertThat(views.get(0).getType()).isEqualTo(FortuneType.LOVE);
        assertThat(views.get(0).getContents()).containsKey(MonthType.JANUARY);

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(client.models).generateContent(
            eq("gemini-test"),
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
    }
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

  private List<FortuneResponse> createFortuneResponses() {
    FortuneResponse love1 = mock(FortuneResponse.class);
    when(love1.getFortune()).thenReturn(FortuneType.LOVE);
    when(love1.getMonth()).thenReturn(MonthType.JANUARY);
    when(love1.getResult()).thenReturn("");

    FortuneResponse love2 = mock(FortuneResponse.class);
    when(love2.getFortune()).thenReturn(FortuneType.LOVE);
    when(love2.getMonth()).thenReturn(MonthType.FEBRUARY);
    when(love2.getResult()).thenReturn("");

    FortuneResponse health1 = mock(FortuneResponse.class);
    when(health1.getFortune()).thenReturn(FortuneType.HEALTH);
    when(health1.getMonth()).thenReturn(MonthType.JANUARY);
    when(health1.getResult()).thenReturn("");

    return Arrays.asList(love1, love2, health1);
  }
}