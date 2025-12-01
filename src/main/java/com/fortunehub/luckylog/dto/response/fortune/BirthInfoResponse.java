package com.fortunehub.luckylog.dto.response.fortune;

import com.fortunehub.luckylog.domain.fortune.CalendarType;
import com.fortunehub.luckylog.domain.fortune.CityType;
import com.fortunehub.luckylog.domain.fortune.GenderType;
import com.fortunehub.luckylog.domain.fortune.TimeType;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BirthInfoResponse {

  private final GenderType gender;
  private final CalendarType calendar;
  private final int year;
  private final int month;
  private final int day;
  private final TimeType time;
  private final CityType city;

  public static BirthInfoResponse from(
      GenderType gender,
      CalendarType calendar,
      LocalDate date,
      TimeType time,
      CityType city
  ) {
    return new BirthInfoResponse(
        gender, calendar,
        date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
        time, city);
  }
}
