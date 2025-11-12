package com.fortunehub.luckylog.client.gemini;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortunehub.luckylog.dto.request.fortune.FortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponseView;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
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
    List<FortuneResponse> responses = generateContent(prompt, request);

    return FortuneResponseView.from(responses);
  }

  private String buildPrompt(FortuneRequest request) {
    int currentYear = LocalDateTime.now().getYear();
    String basePrompt = promptTemplate.replace("[ANALYSIS_YEAR]", String.valueOf(currentYear))
                                      .replace("[FORTUNE_TYPES]",
                                          request.getFortuneTypesAsString());

    return basePrompt + request.toBirthInfo();
  }

  private List<FortuneResponse> generateContent(String prompt, FortuneRequest request) {
    log.info("[GeminiService] [운세 분석 요청] | model={} | fortuneTypes={} | birthInfo={}",
        modelName, request.getFortuneTypesAsString(), request.toBirthInfo().replace("\n", " "));

    long startTime = System.currentTimeMillis();

    try {
      GenerateContentResponse response = client.models.generateContent(
          modelName,
          prompt,
          generateContentConfig
      );

      String responseText = response.text();
      if (responseText == null || responseText.trim().isEmpty()) {
        log.warn("[GeminiService] [API 응답 실패] - [빈 응답 수신] | model={} | fortuneTypes={}",
            modelName, request.getFortuneTypesAsString());

        throw new CustomException(ErrorCode.GEMINI_EMPTY_RESPONSE);
      }

      List<FortuneResponse> responses = parseFortuneResponse(responseText);

      log.info("[GeminiService] [API 응답 성공] | resultCount={}", responses.size());

      return responses;

    } catch (ServerException e) {
      log.error("[GeminiService] [API 호출 실패] - [서버 과부하] | message={}", e.getMessage(), e);
      throw new CustomException(ErrorCode.GEMINI_OVERLOAD);
    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      log.error("[GeminiService] [API 호출 실패] - [알 수 없는 오류] | message={}", e.getMessage(), e);
      throw new CustomException(ErrorCode.GEMINI_UNKNOWN_ERROR, e);
    } finally {
      log.info("[GeminiService] [API 응답 완료] | durationMs={}",
          System.currentTimeMillis() - startTime);
    }
  }

  private List<FortuneResponse> parseFortuneResponse(String jsonResponse) {

    try {
      List<FortuneResponse> responses = objectMapper.readValue(
          jsonResponse.replace("```json", "")
                      .replace("```", "")
                      .trim(),
          new TypeReference<List<FortuneResponse>>() {
          }
      );

      return formatFortuneContent(responses);

    } catch (Exception e) {
      log.error("[GeminiService] [응답 파싱 실패] - [JSON 변환 오류] | message={}", e.getMessage(), e);
      throw new CustomException(ErrorCode.GEMINI_RESPONSE_PARSE_ERROR, e);
    }
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
