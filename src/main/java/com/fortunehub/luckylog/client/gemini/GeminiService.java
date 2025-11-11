package com.fortunehub.luckylog.client.gemini;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortunehub.luckylog.dto.request.fortune.FortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponseView;
import com.google.genai.Client;
import com.google.genai.errors.ServerException;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

  private final Client client;
  private final GenerateContentConfig generateContentConfig;
  private final ObjectMapper objectMapper;

  @Value("${gemini.model}")
  private String modelName;

  @Value("${fortune.prompts.overall}")
  private String promptTemplate;

  public List<FortuneResponseView> analyzeFortune(FortuneRequest request) {

    String prompt = buildPrompt(request);
    List<FortuneResponse> responses = generateContent(prompt);

    return FortuneResponseView.from(responses);
  }

  private String buildPrompt(FortuneRequest request) {
    int currentYear = LocalDateTime.now().getYear();
    String basePrompt = promptTemplate.replace("[ANALYSIS_YEAR]", String.valueOf(currentYear))
                                      .replace("[FORTUNE_TYPES]",
                                          request.getFortuneTypesAsString());

    return new StringBuilder(basePrompt)
        .append(request.toBirthInfo())
        .toString();
  }

  private List<FortuneResponse> generateContent(String prompt) {
    long startTime = System.currentTimeMillis();

    try {
      GenerateContentResponse response = client.models.generateContent(
          modelName,
          prompt,
          generateContentConfig
      );

      long durationMillis = System.currentTimeMillis() - startTime;
      long minutes = durationMillis / 1000 / 60;
      long seconds = (durationMillis / 1000) % 60;
      log.info("Gemini API 응답 완료 - {}분 {}초", minutes, seconds);

      String responseText = response.text();
      if (responseText == null || responseText.trim().isEmpty()) {
        log.warn("Gemini API 빈 응답 수신");
        throw new IllegalStateException("Gemini 응답이 비어있습니다.");
      }

      return parseFortuneResponse(responseText);
    } catch (ServerException e) {
      log.error("Gemini API 호출 실패: 모델: {}, 에러: {}", modelName, e.getMessage(), e);
      throw new IllegalStateException(
          String.format("Gemini API 호출 실패: 모델 %s가 과부하 상태입니다. 잠시 후 다시 시도해주세요.", modelName), e);
    } catch (Exception e) {
      log.error("Gemini API 호출 실패: 모델: {}, 에러: {}", modelName, e.getMessage(), e);
      throw new IllegalStateException(
          String.format("Gemini API 호출 실패: 모델 %s에서 예기치 못한 오류가 발생했습니다.", modelName), e);
    }
  }

  private List<FortuneResponse> parseFortuneResponse(String jsonResponse)
      throws JsonProcessingException {

    List<FortuneResponse> responses = objectMapper.readValue(
        jsonResponse.replace("```json", "")
                    .replace("```", "")
                    .trim(),
        new TypeReference<List<FortuneResponse>>() {
        }
    );

    return formatFortuneContent(responses);
  }

  private List<FortuneResponse> formatFortuneContent(List<FortuneResponse> responses) {

    responses.forEach(response -> {
      if (response.getResult() != null) {
        response.setResult(response.getResult().replace(" | ", "\n"));
      }
    });
    return responses;
  }
}
