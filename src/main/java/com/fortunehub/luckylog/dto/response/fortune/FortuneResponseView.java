package com.fortunehub.luckylog.dto.response.fortune;

import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.fortune.MonthType;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class FortuneResponseView {

  private FortuneType type;
  private Map<MonthType, String> contents;

  public static List<FortuneResponseView> from(List<FortuneResponse> responses) {
    return responses.stream()
                    .collect(Collectors.groupingBy(
                        FortuneResponse::getFortune,
                        LinkedHashMap::new, // 운세별 정렬
                        Collectors.toList()))
                    .entrySet()
                    .stream()
                    .map(entry -> {
                      FortuneResponseView view = new FortuneResponseView();
                      view.setType(entry.getKey());

                      // List<FortuneResponse> → Map<MonthType, String> 변환
                      Map<MonthType, String> monthlyContents = entry.getValue()
                                                                    .stream()
                                                                    .collect(Collectors.toMap(
                                                                        FortuneResponse::getMonth, // Key: MonthType
                                                                        FortuneResponse::getResult, // Value: String (result 내용)
                                                                        (existing, replacement) -> existing,
                                                                        LinkedHashMap::new // 월별 정렬
                                                                    ));

                      view.setContents(monthlyContents);

                      return view;
                    })
                    .toList();
  }
}
