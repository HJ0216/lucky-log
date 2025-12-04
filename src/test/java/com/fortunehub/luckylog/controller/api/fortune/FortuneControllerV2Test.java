package com.fortunehub.luckylog.controller.api.fortune;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
import com.fortunehub.luckylog.exception.CustomException;
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
@WebMvcTest(FortuneControllerV2.class) // 웹 계층만 로드해서 테스트
@DisplayName("운세 Controller V2")
class FortuneControllerV2Test {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private FortuneService fortuneService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("정상적인 운세 저장 요청 시 운세가 저장되고 201 응답한다")
  @WithMockCustomUser
  void save_WhenValidRequest_ThenReturnsOk() throws Exception {
    // given
    List<FortuneType> fortuneTypes = List.of(FortuneType.LOVE, FortuneType.HEALTH);
    SaveFortuneRequest request = createValidFortuneRequest(fortuneTypes);

    Long savedId = 1L;
    when(fortuneService.save(any(Member.class), any(SaveFortuneRequest.class)))
        .thenReturn(savedId);

    // when & then
    mockMvc.perform(post("/api/v2/fortunes")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isCreated())
           .andExpect(header().string("Location", endsWith("/api/v2/fortunes/1")));

    verify(fortuneService).save(any(Member.class), any(SaveFortuneRequest.class));
  }

  @Test
  @DisplayName("인증되지 않은 사용자는 401 응답한다")
  void save_WhenUnauthorized_ThenReturnsUnauthorized() throws Exception {
    // given
    List<FortuneType> fortuneTypes = List.of(FortuneType.LOVE, FortuneType.HEALTH);
    SaveFortuneRequest request = createValidFortuneRequest(fortuneTypes);

    // when & then
    mockMvc.perform(post("/api/v2/fortunes")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isUnauthorized());

    verify(fortuneService, never()).save(any(), any());
  }

  @Test
  @DisplayName("생년월일 정보가 없으면 400을 응답한다")
  @WithMockCustomUser
  void save_WhenNoBirthInfo_ThenReturnsBadRequest() throws Exception {
    // given
    List<FortuneType> fortuneTypes = List.of(FortuneType.LOVE, FortuneType.HEALTH);
    SaveFortuneRequest request = createValidFortuneRequest(fortuneTypes);
    request.setBirthInfo(null);

    // when & then
    mockMvc.perform(post("/api/v2/fortunes")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
           .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다."))
           .andExpect(jsonPath("$.timestamp").exists())
           .andExpect(jsonPath("$.details.birthInfo").exists());

    verify(fortuneService, never()).save(any(), any());
  }

  @Test
  @DisplayName("이미 존재하는 운세 제목이면 409를 응답한다")
  @WithMockCustomUser
  void save_WhenTitleIsDuplicate_ThenReturnsConflict() throws Exception {
    // given
    List<FortuneType> fortuneTypes = List.of(FortuneType.LOVE, FortuneType.HEALTH);
    SaveFortuneRequest request = createValidFortuneRequest(fortuneTypes);

    when(fortuneService.save(any(Member.class), any(SaveFortuneRequest.class)))
        .thenThrow(new CustomException(ErrorCode.DUPLICATE_FORTUNE_TITLE));

    // when & then
    mockMvc.perform(post("/api/v2/fortunes")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isConflict())
           .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_FORTUNE_TITLE.name()))
           .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_FORTUNE_TITLE.getMessage()))
           .andExpect(jsonPath("$.timestamp").exists());

    verify(fortuneService)
        .save(any(Member.class), any(SaveFortuneRequest.class));
  }

  @Test
  @DisplayName("예상치 못한 오류 발생 시, 500 응답한다")
  @WithMockCustomUser
  void save_WhenServiceThrowsException_ThenReturnsInternalServerError() throws Exception {
    // given
    List<FortuneType> fortuneTypes = List.of(FortuneType.OVERALL, FortuneType.MONEY);
    SaveFortuneRequest request = createValidFortuneRequest(fortuneTypes);

    willThrow(new RuntimeException("예상치 못한 오류 발생"))
        .given(fortuneService).save(any(Member.class), any(SaveFortuneRequest.class));

    // when & then
    mockMvc.perform(post("/api/v2/fortunes")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isInternalServerError())
           .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
           .andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다."))
           .andExpect(jsonPath("$.timestamp").exists());

    verify(fortuneService)
        .save(any(Member.class), any(SaveFortuneRequest.class));

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
    response1.setFortune(FortuneType.LOVE);
    response1.setPeriodValue(PeriodValue.JANUARY);
    response1.setResult("좋은 한 해가 될 것입니다.");

    FortuneResponse response2 = new FortuneResponse();
    response2.setFortune(FortuneType.HEALTH);
    response2.setPeriodValue(PeriodValue.FEBRUARY);
    response2.setResult("건강운이 상승합니다.");
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