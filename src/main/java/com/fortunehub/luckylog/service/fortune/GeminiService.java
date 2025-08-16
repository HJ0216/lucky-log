package com.fortunehub.luckylog.service.fortune;

import com.fortunehub.luckylog.dto.request.fortune.FortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResult;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import java.time.LocalDateTime;
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

  @Value("${gemini.model}")
  private String modelName;

  @Value("${fortune.prompts.overall}")
  private String promptTemplate;

  public FortuneResult analyzeFortune(FortuneRequest request) {

    String prompt = buildPrompt(request);
    String response = generateContent(prompt);

    return FortuneResult.builder().overall(response).build();
  }

  private String buildPrompt(FortuneRequest request) {
    int currentYear = LocalDateTime.now().getYear();
    String basePrompt = promptTemplate.replace("[ANALYSIS_YEAR]", String.valueOf(currentYear));

    return new StringBuilder(basePrompt)
        .append(request.toPromptString())
        .toString();
  }

  private String generateContent(String prompt) {
    long startTime = System.currentTimeMillis();

    try {
      GenerateContentResponse response = client.models.generateContent(
          modelName,
          prompt,
          generateContentConfig
      );

      long duration = System.currentTimeMillis() - startTime;
      log.info("Gemini API 응답 완료 - {}ms", duration);

      String responseText = response.text();
      if (responseText == null || responseText.trim().isEmpty()) {
        log.warn("Gemini API 빈 응답 수신");
        throw new IllegalStateException("Gemini 응답이 비어있습니다.");
      }

      return responseText;
    } catch (Exception e) {
      log.error("Gemini API 호출 실패: 모델: {}, 에러: {}", modelName, e.getMessage(), e);
      throw new IllegalStateException("Gemini API 호출에 실패하였습니다.", e);
    }
  }
}
