package com.fortunehub.luckylog.controller.api.fortune;

import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.dto.request.fortune.SaveFortuneRequest;
import com.fortunehub.luckylog.dto.response.common.ApiResponse;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import com.fortunehub.luckylog.security.CustomUserDetails;
import com.fortunehub.luckylog.service.fortune.FortuneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/fortune")
public class FortuneController {

  private final FortuneService fortuneService;

  @PostMapping
  public ResponseEntity<ApiResponse> save(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @SessionAttribute(name = "birthInfo", required = false) BirthInfoForm birthInfo,
      @Valid @RequestBody SaveFortuneRequest request
  ) {
    if (birthInfo == null) {
      log.warn("[운세 저장 실패] - [세션 데이터 누락] | 생년월일 정보가 세션에 저장되지 않음");
      return ResponseEntity.badRequest()
                           .body(ApiResponse.error(ErrorCode.BIRTH_INFO_REQUIRED.getMessage()));
    }

    try {
      fortuneService.save(userDetails.getMember(), request, birthInfo);

      log.info("[운세 저장 완료]");

      return ResponseEntity.ok(ApiResponse.success("저장되었습니다."));

    } catch (CustomException e) {
      log.warn("[운세 저장 실패]", e);

      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                           .body(ApiResponse.error(e.getMessage()));

    } catch (Exception e) {
      log.error("[운세 저장 실패]", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                           .body(
                               ApiResponse.error(ErrorCode.FORTUNE_SAVE_SYSTEM_ERROR.getMessage()));
    }
  }
}
