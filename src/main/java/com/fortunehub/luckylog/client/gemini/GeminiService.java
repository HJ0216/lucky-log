package com.fortunehub.luckylog.client.gemini;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortunehub.luckylog.dto.request.fortune.FortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import com.google.genai.Client;
import com.google.genai.errors.ServerException;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GeminiService {

  private final String modelName;
  private final String promptTemplate;

  private final Client client;
  private final GenerateContentConfig generateContentConfig;
  private final ObjectMapper objectMapper;

  public GeminiService(
      Client client,
      GenerateContentConfig generateContentConfig,
      ObjectMapper objectMapper,
      @Value("${gemini.model}") String modelName,
      @Value("${fortune.prompt}") String promptTemplate
  ) {
    this.client = client;
    this.generateContentConfig = generateContentConfig;
    this.objectMapper = objectMapper;
    this.modelName = modelName;
    this.promptTemplate = promptTemplate;
  }

  public List<FortuneResponse> generateFortune(FortuneRequest request) {

    String prompt = buildPrompt(request);
    List<FortuneResponse> responses = generateContent(prompt, request);

    return responses;
  }

  private String buildPrompt(FortuneRequest request) {
    String basePrompt = promptTemplate.replace("[ANALYSIS_YEAR]",
                                          String.valueOf(request.getFortuneResultYear()))
                                      .replace("[FORTUNE_TYPES]",
                                          request.getFortuneTypesAsString());

    return basePrompt + request.toBirthInfo();
  }

  private List<FortuneResponse> generateContent(String prompt, FortuneRequest request) {
    log.info("[운세 분석 요청] | model={} | fortuneTypes={} | birthInfo={}",
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
        log.warn("[API 응답 실패] - [빈 응답 수신] | model={} | fortuneTypes={}",
            modelName, request.getFortuneTypesAsString());

        throw new CustomException(ErrorCode.GEMINI_EMPTY_RESPONSE);
      }

      List<FortuneResponse> responses = parseFortuneResponse(responseText);

      log.info("[API 응답 성공] | resultCount={}", responses.size());

      return responses;

    } catch (ServerException e) {
      log.error("[API 호출 실패] - [서버 과부하] | message={}", e.getMessage(), e);
      throw new CustomException(ErrorCode.GEMINI_OVERLOAD);
    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      log.error("[API 호출 실패] - [알 수 없는 오류] | message={}", e.getMessage(), e);
      throw new CustomException(ErrorCode.GEMINI_UNKNOWN_ERROR, e);
    } finally {
      log.info("[API 응답 완료] | durationMs={}",
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
      log.error("[응답 파싱 실패] - [JSON 변환 오류] | message={}", e.getMessage(), e);
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
