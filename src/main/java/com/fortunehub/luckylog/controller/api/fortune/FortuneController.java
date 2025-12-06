package com.fortunehub.luckylog.controller.api.fortune;

import com.fortunehub.luckylog.dto.request.fortune.SaveFortuneRequest;
import com.fortunehub.luckylog.dto.response.common.ApiResponse;
import com.fortunehub.luckylog.security.CustomUserDetails;
import com.fortunehub.luckylog.service.fortune.FortuneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/fortune")
public class FortuneController {

  private final FortuneService fortuneService;

  @PostMapping
  public ResponseEntity<ApiResponse> save(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody SaveFortuneRequest request
  ) {
    fortuneService.save(userDetails.getMemberId(), request);

    log.info("[운세 저장 완료] | memberId={}", userDetails.getMemberId());

    return ResponseEntity.ok(ApiResponse.success("저장되었습니다."));
  }
}
