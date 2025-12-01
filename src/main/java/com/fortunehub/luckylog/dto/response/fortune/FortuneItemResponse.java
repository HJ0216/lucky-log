package com.fortunehub.luckylog.dto.response.fortune;

import com.fortunehub.luckylog.domain.fortune.FortuneResultItem;
import com.fortunehub.luckylog.domain.fortune.PeriodValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FortuneItemResponse {

  private final Long id;
  private final PeriodValue periodValue;
  private final String content;
  private final Integer accuracy;

  public static FortuneItemResponse from(FortuneResultItem item) {
    return new FortuneItemResponse(
        item.getId(),
        item.getPeriodValue(),
        item.getContent(),
        item.getAccuracy()
    );
  }
}
