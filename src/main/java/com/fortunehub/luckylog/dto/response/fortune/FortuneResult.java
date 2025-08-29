package com.fortunehub.luckylog.dto.response.fortune;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // JSON으로 변환 시 null인 필드는 제외
@ToString
@Builder
public class FortuneResult {

  private String overall;
  private String money;
  private String love;
  private String career;
  private String study;
  private String luck;
  private String family;
  private String health;
}
