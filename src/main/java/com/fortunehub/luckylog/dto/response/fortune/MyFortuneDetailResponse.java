package com.fortunehub.luckylog.dto.response.fortune;

import com.fortunehub.luckylog.domain.fortune.FortuneResult;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyFortuneDetailResponse {

  private final Long id;
  private final String title;
  private final int resultYear;
  private final BirthInfoResponse birthInfo;
  private final FortuneOptionResponse fortuneOption;
  private final List<FortuneItemResponse> items;

  public static MyFortuneDetailResponse from(FortuneResult result) {
    return new MyFortuneDetailResponse(
        result.getId(),
        result.getTitle(),
        result.getResultYear(),
        BirthInfoResponse.from(
            result.getGender(),
            result.getCalendar(),
            result.getBirthDate(),
            result.getBirthTimeZone(),
            result.getBirthRegion()
        ),
        FortuneOptionResponse.from(
            result.getAiType(),
            result.getCategories(),
            result.getPeriodType()
        ),
        result.getItems().stream()
              .map(FortuneItemResponse::from)
              .toList()
    );
  }
}
