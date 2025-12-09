package com.fortunehub.luckylog.dto.response.fortune;

import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.fortune.PeriodValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "AI가 생성한 운세 결과")
@NoArgsConstructor
@Setter
@Getter
public class FortuneResponse implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Schema(description = "운세 종류", example = "love")
  private FortuneType fortune;
  @Schema(description = "기간 값", example = "january")
  private PeriodValue periodValue;
  @Schema(description = "AI가 생성한 운세 내용", example = "좋은 한 해가 될 것입니다. 사랑운이 상승하는 시기입니다.")
  private String result;
}
