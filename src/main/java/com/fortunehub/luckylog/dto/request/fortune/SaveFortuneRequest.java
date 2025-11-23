package com.fortunehub.luckylog.dto.request.fortune;

import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaveFortuneRequest {

  @NotBlank(message = "😵 제목은 필수 입니다!")
  private String title;
  
  @NotNull(message = "😵 운세 결과 연도는 필수입니다!")
  private Integer fortuneResultYear;

  @NotNull(message = "😵 운세 옵션 정보를 찾을 수 없습니다!")
  @Valid // 중첩 객체도 검증
  private FortuneOptionForm option;

  @NotEmpty(message = "😵 운세 결과를 찾을 수 없습니다!")
  @Valid // 리스트 내부 객체도 검증
  private List<FortuneResponse> responses = new ArrayList<>();

}
