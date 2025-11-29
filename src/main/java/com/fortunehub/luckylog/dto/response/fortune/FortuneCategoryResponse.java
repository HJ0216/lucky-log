package com.fortunehub.luckylog.dto.response.fortune;

import com.fortunehub.luckylog.domain.fortune.FortuneResultCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FortuneCategoryResponse {

  private final FortuneType type;

  public static FortuneCategoryResponse from(FortuneResultCategory category) {
    return new FortuneCategoryResponse(category.getFortuneCategory().getFortuneType());
  }
}
