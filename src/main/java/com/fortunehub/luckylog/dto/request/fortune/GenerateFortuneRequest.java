package com.fortunehub.luckylog.dto.request.fortune;

import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "운세 생성 요청")
@Setter
@Getter
public class GenerateFortuneRequest {

  @Schema(description = "사용자 생년월일 정보", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "😵 생년 월일 정보를 찾을 수 없습니다!")
  @Valid
  private BirthInfoForm birthInfo;

  @Schema(description = "운세 옵션 정보 (AI 타입, 운세 종류, 주기)", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "😵 운세 옵션 정보를 찾을 수 없습니다!")
  @Valid // 중첩 객체도 검증
  private FortuneOptionForm option;
}
