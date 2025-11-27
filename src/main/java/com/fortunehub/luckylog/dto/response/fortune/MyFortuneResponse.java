package com.fortunehub.luckylog.dto.response.fortune;

import com.fortunehub.luckylog.domain.fortune.FortuneCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneResult;
import com.fortunehub.luckylog.domain.fortune.FortuneResultCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyFortuneResponse {

  private Long id;
  private String title;
  private String fortuneTypeDisplayName;
  private String createdAt;

  public static MyFortuneResponse from(FortuneResult result) {
    MyFortuneResponse response = new MyFortuneResponse();
    response.id = result.getId();
    response.title = result.getTitle();
    response.fortuneTypeDisplayName = generateFortuneTypeDisplayName(result.getCategories());
    response.createdAt = getFormattedCreatedAt(result.getCreatedAt());

    return response;
  }

  private static String generateFortuneTypeDisplayName(List<FortuneResultCategory> resultCategories) {
    List<String> types = resultCategories.stream()
                                         .map(FortuneResultCategory::getFortuneCategory)
                                         .map(FortuneCategory::getFortuneType)
                                         .map(FortuneType::getDisplayString)
                                         .toList();

    return types.size() == 1
        ? types.get(0)
        : types.get(0) + " 외 " + (types.size() - 1) + "개 운세";
  }

  private static String getFormattedCreatedAt(LocalDateTime createdAt) {
    return createdAt.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
  }
}
