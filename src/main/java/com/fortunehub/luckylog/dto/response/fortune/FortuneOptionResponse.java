package com.fortunehub.luckylog.dto.response.fortune;

import com.fortunehub.luckylog.domain.fortune.AIType;
import com.fortunehub.luckylog.domain.fortune.FortuneResultCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.fortune.PeriodType;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FortuneOptionResponse {

  private final AIType ai;
  private final List<FortuneType> types;
  private final PeriodType period;

  public static FortuneOptionResponse from(
      AIType ai,
      Set<FortuneResultCategory> resultCategories,
      PeriodType period
  ) {
    return new FortuneOptionResponse(
        ai,
        resultCategories.stream()
                        .map(rc -> rc.getFortuneCategory().getFortuneType())
                        .toList(),
        period
    );
  }
}
