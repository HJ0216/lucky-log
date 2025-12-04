package com.fortunehub.luckylog.dto.request.fortune;

import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "운세 저장 요청")
@Setter
@Getter
public class SaveFortuneRequest {

  @Schema(description = "운세 제목", example = "2025년 나의 운세", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "😵 제목은 필수 입니다!")
  private String title;

  @Schema(description = "운세 결과 연도", example = "2025", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "😵 운세 결과 연도는 필수입니다!")
  private Integer fortuneResultYear;

  @Schema(description = "운세 옵션 정보 (AI 타입, 운세 종류, 주기)", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "😵 운세 옵션 정보를 찾을 수 없습니다!")
  @Valid // 중첩 객체도 검증
  private FortuneOptionForm option;

  @Schema(description = "AI가 생성한 운세 결과 목록", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotEmpty(message = "😵 운세 결과를 찾을 수 없습니다!")
  @Valid // 리스트 내부 객체도 검증
  private List<FortuneResponse> responses = new ArrayList<>();

  @Schema(description = "사용자 생년월일 정보", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "😵 생년 월일 정보를 찾을 수 없습니다!")
  @Valid
  private BirthInfoForm birthInfo;
}
