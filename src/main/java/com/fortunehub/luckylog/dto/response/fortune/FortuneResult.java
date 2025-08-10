package com.fortunehub.luckylog.dto.response.fortune;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // JSON으로 변환 시 null인 필드는 제외
public class FortuneResult {

  private String overall = "전반적으로 안정된 기운이 흐르는 시기입니다. 새로운 시작을 위한 준비를 하기에 좋은 때이며, 과거의 경험을 바탕으로 현명한 선택을 할 수 있을 것입니다.";
  private String money;
  private String love = "사랑에 있어서는 진실한 마음이 통하는 시기입니다. 상대방을 이해하려는 노력과 소통이 관계 발전의 열쇠가 될 것입니다.";
  private String career;
  private String study;
  private String luck;
  private String family;
  private String health;
}
