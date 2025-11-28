package com.fortunehub.luckylog.dto.response.fortune;

import com.fortunehub.luckylog.domain.fortune.FortuneCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneResult;
import com.fortunehub.luckylog.domain.fortune.FortuneResultCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyFortuneResponse {

  private final Long id;
  private final String title;
  private final String fortuneTypeDisplayName;
  private final String createdAt;

  public static MyFortuneResponse from(FortuneResult result) {
    return new MyFortuneResponse(
        result.getId(),
        result.getTitle(),
        generateFortuneTypeDisplayName(result.getCategories()),
        getFormattedCreatedAt(result.getCreatedAt()));
  }

  private static String generateFortuneTypeDisplayName(
      Set<FortuneResultCategory> resultCategories) {
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
