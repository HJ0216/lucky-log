package com.fortunehub.luckylog.domain.fortune;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CityType {

  SEOUL("서울특별시"),
  GYEONGGI("경기도"),
  INCHEON("인천광역시"),
  GANGWON("강원도"),
  CHUNGCHEONG("충청도"),
  JEOLLA("전라도"),
  GYEONGSANG("경상도"),
  JEJU("제주특별자치도"),
  UNKNOWN("모름");

  private final String displayName;
}
