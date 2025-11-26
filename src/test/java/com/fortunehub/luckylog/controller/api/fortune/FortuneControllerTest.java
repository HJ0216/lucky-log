package com.fortunehub.luckylog.controller.api.fortune;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortunehub.luckylog.config.SecurityConfig;
import com.fortunehub.luckylog.config.WithMockCustomUser;
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
import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.dto.request.fortune.SaveFortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import com.fortunehub.luckylog.exception.ErrorCode;
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
@WebMvcTest(FortuneController.class)
// 웹 계층만 로드해서 테스트
@DisplayName("운세 Controller")
class FortuneControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private FortuneService fortuneService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("정상적인 운세 저장 요청 시 운세가 저장되고 200 응답한다")
  @WithMockCustomUser
  void save_WhenValidRequest_ThenReturnsOk() throws Exception {
    // given
    List<FortuneType> fortuneTypes = List.of(FortuneType.OVERALL, FortuneType.MONEY);
    SaveFortuneRequest request = createValidFortuneRequest(fortuneTypes);

    // when & then
    mockMvc.perform(post("/api/fortune")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.success").value(true))
           .andExpect(jsonPath("$.message").value("저장되었습니다."));

    verify(fortuneService).save(any(Member.class), any(SaveFortuneRequest.class));
  }

  @Test
  @DisplayName("인증되지 않은 사용자는 401 응답한다")
  void save_WhenUnauthorized_ThenReturnsUnauthorized() throws Exception {
    // given
    BirthInfoForm birthInfo = createValidBirthInfo();
    List<FortuneType> fortuneTypes = List.of(FortuneType.OVERALL, FortuneType.MONEY);
    SaveFortuneRequest request = createValidFortuneRequest(fortuneTypes);

    // when & then
    mockMvc.perform(post("/api/fortune")
               .sessionAttr("birthInfo", birthInfo)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("세션에 생년월일 정보가 없으면 400을 응답한다")
  @WithMockCustomUser
  void save_WhenNoBirthInfo_ThenReturnsBadRequest() throws Exception {
    // given
    List<FortuneType> fortuneTypes = List.of(FortuneType.OVERALL, FortuneType.MONEY);
    SaveFortuneRequest request = createValidFortuneRequest(fortuneTypes);
    request.setBirthInfo(null);

    // when & then
    mockMvc.perform(post("/api/fortune")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.success").value(false))
           .andExpect(jsonPath("$.message").value(ErrorCode.ARGUMENT_NOT_VALID.getMessage()));

    verify(fortuneService, never()).save(any(), any());
  }

  @Test
  @DisplayName("유효하지 않은 요청 시 400 응답한다")
  @WithMockCustomUser
  void save_WhenInvalidRequest_ThenReturnsBadRequest() throws Exception {
    BirthInfoForm birthInfo = createValidBirthInfo();
    SaveFortuneRequest invalidRequest = new SaveFortuneRequest();

    // when & then
    mockMvc.perform(post("/api/fortune")
               .sessionAttr("birthInfo", birthInfo)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(invalidRequest)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message").value(ErrorCode.ARGUMENT_NOT_VALID.getMessage()));

    verify(fortuneService, never()).save(any(), any());
  }

  @Test
  @DisplayName("예상치 못한 오류 발생 시, 500 응답한다")
  @WithMockCustomUser
  void save_WhenServiceThrowsException_ThenReturnsInternalServerError() throws Exception {
    // given
    List<FortuneType> fortuneTypes = List.of(FortuneType.OVERALL, FortuneType.MONEY);
    SaveFortuneRequest request = createValidFortuneRequest(fortuneTypes);

    doThrow(new RuntimeException("예상치 못한 오류 발생"))
        .when(fortuneService).save(any(Member.class), any(SaveFortuneRequest.class));

    // when & then
    mockMvc.perform(post("/api/fortune")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isInternalServerError())
           .andExpect(jsonPath("$.success").value(false))
           .andExpect(jsonPath("$.message").value(ErrorCode.SYSTEM_ERROR.getMessage()));
  }

  private SaveFortuneRequest createValidFortuneRequest(List<FortuneType> fortunes) {
    FortuneOptionForm option = createValidFortuneOption(fortunes);
    List<FortuneResponse> responses = createValidFortuneResponses();

    SaveFortuneRequest request = new SaveFortuneRequest();
    request.setTitle("2025년 운세");
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
}