package com.fortunehub.luckylog.controller.api.fortune;

import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import com.fortunehub.luckylog.dto.request.fortune.GenerateFortuneRequest;
import com.fortunehub.luckylog.service.fortune.FortuneService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/external/fortunes")
@Tag(name = "운세 API", description = "운세 생성 관련 API")
public class ExternalFortuneController {

  private final FortuneService fortuneService;

  @PostMapping
  public ResponseEntity<List<FortuneResponse>> generate(
      @Valid @RequestBody GenerateFortuneRequest request) {

    int fortuneResultYear = LocalDateTime.now().getYear();
    List<FortuneResponse> responses = fortuneService.generateFortune(
        request.getBirthInfo(), request.getOption(), fortuneResultYear);

    return ResponseEntity.ok(responses);
  }
}
