package com.fortunehub.luckylog.controller.api.fortune;

import com.fortunehub.luckylog.dto.request.fortune.GenerateFortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import com.fortunehub.luckylog.service.fortune.FortuneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
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
  @Operation(summary = "외부 API를 활용한 운세 생성")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "운세 생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 회원/운세/운세 카테고리"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<List<FortuneResponse>> generate(
      HttpSession session,
      @Valid @RequestBody GenerateFortuneRequest request) {

    int fortuneResultYear = LocalDateTime.now().getYear();
    List<FortuneResponse> responses = fortuneService.generateFortune(
        session.getId(), request.getBirthInfo(), request.getOption(), fortuneResultYear);

    log.info("[외부 운세 생성 완료] | fortuneTypes={} | resultCount={}",
        request.getOption().getFortunes(), responses.size());

    return ResponseEntity.ok(responses);
  }
}
