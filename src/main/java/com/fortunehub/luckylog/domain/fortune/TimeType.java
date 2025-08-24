package com.fortunehub.luckylog.domain.fortune;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TimeType {

  TIME_23_30("자시(23:30 - 01:29)"),
  TIME_01_30("축시(01:30 - 03:29)"),
  TIME_03_30("인시(03:30 - 05:29)"),
  TIME_05_30("묘시(05:30 - 07:29)"),
  TIME_07_30("진시(07:30 - 09:29)"),
  TIME_09_30("사시(09:30 - 11:29)"),
  TIME_11_30("오시(11:30 - 13:29)"),
  TIME_13_30("미시(13:30 - 15:29)"),
  TIME_15_30("신시(15:30 - 17:29)"),
  TIME_17_30("유시(17:30 - 19:29)"),
  TIME_19_30("술시(19:30 - 21:29)"),
  TIME_21_30("해시(21:30 - 23:29)");

  private final String displayName;
}
