package com.fortunehub.luckylog.controller.api.fortune;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortunehub.luckylog.config.SecurityConfig;
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
import com.fortunehub.luckylog.dto.request.fortune.GenerateFortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import com.fortunehub.luckylog.service.fortune.FortuneService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Import(SecurityConfig.class)
@WebMvcTest(ExternalFortuneController.class) // 웹 계층만 로드해서 테스트
@DisplayName("운세 외부 API Controller V2")
class ExternalFortuneControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private FortuneService fortuneService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("정상적인 운세 생성 요청 시 운세가 생성되고 200 응답한다")
  void generateFortune_WhenValidRequest_ThenReturnsOk() throws Exception {
    // given
    GenerateFortuneRequest request = createGenerateFortuneRequest();
    List<FortuneResponse> responses = createFortuneResponses();

    when(fortuneService.generateFortune(
        any(BirthInfoForm.class),
        any(FortuneOptionForm.class),
        anyInt()
    )).thenReturn(responses);

    // when & then
    mockMvc.perform(post("/api/v2/external/fortunes")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].fortune").value("love"))
           .andExpect(jsonPath("$[0].periodValue").value("january"))
           .andExpect(jsonPath("$[0].result").value("좋은 한 해가 될 것입니다."))
           .andExpect(jsonPath("$[1].fortune").value("health"))
           .andExpect(jsonPath("$[1].periodValue").value("february"))
           .andExpect(jsonPath("$[1].result").value("건강운이 상승합니다."));

    verify(fortuneService).generateFortune(
        any(BirthInfoForm.class),
        any(FortuneOptionForm.class),
        anyInt());
  }

  @Test
  @DisplayName("생년월일 정보가 없으면 400을 응답한다")
  void generateFortune_WhenBirthInfoIsNull_ThenReturnsBadRequest() throws Exception {
    // given
    GenerateFortuneRequest request = createGenerateFortuneRequest();
    request.setBirthInfo(null);

    // when & then
    mockMvc.perform(post("/api/v2/external/fortunes")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
           .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다."))
           .andExpect(jsonPath("$.timestamp").exists());


    verify(fortuneService, never()).generateFortune(
        any(BirthInfoForm.class),
        any(FortuneOptionForm.class),
        anyInt());
  }

  @Test
  @DisplayName("운세 옵션 정보가 없으면 400을 응답한다")
  void generateFortune_WhenFortuneOptionIsNull_ThenReturnsBadRequest() throws Exception {
    // given
    GenerateFortuneRequest request = createGenerateFortuneRequest();
    request.setOption(null);

    // when & then
    mockMvc.perform(post("/api/v2/external/fortunes")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
           .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다."))
           .andExpect(jsonPath("$.timestamp").exists());


    verify(fortuneService, never()).generateFortune(
        any(BirthInfoForm.class),
        any(FortuneOptionForm.class),
        anyInt());
  }

  @Test
  @DisplayName("예상치 못한 오류가 발생하면 500을 응답한다")
  void generateFortune_WhenUnexpectedErrorThrows_ThenReturnsInternalServerError() throws Exception {
    // given
    GenerateFortuneRequest request = createGenerateFortuneRequest();

    when(fortuneService.generateFortune(
        any(BirthInfoForm.class),
        any(FortuneOptionForm.class),
        anyInt()))
        .thenThrow(new RuntimeException("예상치 못한 오류 발생"));

    // when & then
    mockMvc.perform(post("/api/v2/external/fortunes")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isInternalServerError())
           .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
           .andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다."))
           .andExpect(jsonPath("$.timestamp").exists());

    verify(fortuneService).generateFortune(
        any(BirthInfoForm.class),
        any(FortuneOptionForm.class),
        anyInt());
  }

  private List<FortuneResponse> createFortuneResponses() {
    FortuneResponse response1 = new FortuneResponse();
    response1.setFortune(FortuneType.LOVE);
    response1.setPeriodValue(PeriodValue.JANUARY);
    response1.setResult("좋은 한 해가 될 것입니다.");

    FortuneResponse response2 = new FortuneResponse();
    response2.setFortune(FortuneType.HEALTH);
    response2.setPeriodValue(PeriodValue.FEBRUARY);
    response2.setResult("건강운이 상승합니다.");
    return List.of(response1, response2);
  }

  private GenerateFortuneRequest createGenerateFortuneRequest() {
    FortuneOptionForm option = createFortuneOption();
    BirthInfoForm birthInfo = createBirthInfo();

    GenerateFortuneRequest request = new GenerateFortuneRequest();
    request.setBirthInfo(birthInfo);
    request.setOption(option);

    return request;
  }

  private FortuneOptionForm createFortuneOption() {
    FortuneOptionForm option = new FortuneOptionForm();
    option.setAi(AIType.GEMINI);
    option.setFortunes(List.of(FortuneType.LOVE, FortuneType.HEALTH));
    option.setPeriod(PeriodType.MONTHLY);
    return option;
  }

  private BirthInfoForm createBirthInfo() {
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
}