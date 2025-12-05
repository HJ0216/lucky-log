package com.fortunehub.luckylog.controller.api.fortune;

import com.fortunehub.luckylog.dto.request.fortune.SaveFortuneRequest;
import com.fortunehub.luckylog.security.CustomUserDetails;
import com.fortunehub.luckylog.service.fortune.FortuneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/fortunes")
@Tag(name = "운세 API", description = "운세 저장 관련 API")
public class FortuneControllerV2 {

  private final FortuneService fortuneService;

  @PostMapping
  @Operation(summary = "운세 저장")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "운세 생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "401", description = "인증 실패"),
      @ApiResponse(responseCode = "403", description = "운세 저장 횟수 초과 (최대 5개)"),
      @ApiResponse(responseCode = "404", description = "존재하지 않는 회원/운세/운세 카테고리"),
      @ApiResponse(responseCode = "409", description = "중복된 운세 제목"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  public ResponseEntity<Void> save(
      @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody SaveFortuneRequest request
  ) {
    Long savedId = fortuneService.save(userDetails.getMember(), request);

    log.info("[운세 저장 완료] | memberId={}", userDetails.getMember().getId());

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(savedId)
        .toUri();

    return ResponseEntity
        .created(location)
        .build();
  }
}
