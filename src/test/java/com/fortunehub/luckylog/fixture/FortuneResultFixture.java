package com.fortunehub.luckylog.fixture;

import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.domain.fortune.AIType;
import com.fortunehub.luckylog.domain.fortune.CalendarType;
import com.fortunehub.luckylog.domain.fortune.CityType;
import com.fortunehub.luckylog.domain.fortune.FortuneCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneResult;
import com.fortunehub.luckylog.domain.fortune.FortuneResultCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneResultItem;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.fortune.GenderType;
import com.fortunehub.luckylog.domain.fortune.PeriodType;
import com.fortunehub.luckylog.domain.fortune.PeriodValue;
import com.fortunehub.luckylog.domain.fortune.TimeType;
import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.dto.request.fortune.SaveFortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.springframework.test.util.ReflectionTestUtils;

public class FortuneResultFixture {

  private static final Map<FortuneType, FortuneCategory> CATEGORY_MAP;

  static {
    CATEGORY_MAP = Map.of(
        FortuneType.OVERALL, FortuneCategory.create(1, FortuneType.OVERALL),
        FortuneType.MONEY, FortuneCategory.create(2, FortuneType.MONEY),
        FortuneType.LOVE, FortuneCategory.create(3, FortuneType.LOVE),
        FortuneType.CAREER, FortuneCategory.create(4, FortuneType.CAREER),
        FortuneType.STUDY, FortuneCategory.create(5, FortuneType.STUDY),
        FortuneType.LUCK, FortuneCategory.create(6, FortuneType.LUCK),
        FortuneType.FAMILY, FortuneCategory.create(7, FortuneType.FAMILY),
        FortuneType.HEALTH, FortuneCategory.create(8, FortuneType.HEALTH)
    );
  }

  private FortuneResultFixture() {
  }

  // Public Methods
  public static FortuneResult createFortuneResultWithTitle(Member member, String title) {
    SaveFortuneRequest request = createSaveFortuneRequest(title);
    FortuneResult result = FortuneResult.create(member, request);

    ReflectionTestUtils.setField(result, "createdAt", LocalDateTime.now());
    ReflectionTestUtils.setField(result, "updatedAt", LocalDateTime.now());

    addItems(result, request.getResponses());
    addCategories(result, request.getOption().getFortunes());

    return result;
  }

  public static FortuneResult createFortuneResult(Member member) {
    return createFortuneResultWithTitle(member, "2025년 월별 운세");
  }

  public static List<FortuneResult> createFortuneResults(Member member, int count) {
    return IntStream.range(0, count)
                    .mapToObj(i -> createFortuneResultWithTitle(member, "2025년 월별 운세 " + (i + 1)))
                    .toList();
  }

  public static FortuneResult createFortuneResultWithTitleAndId(Member member, String title) {
    SaveFortuneRequest request = createSaveFortuneRequest(title);
    FortuneResult result = FortuneResult.create(member, request);

    ReflectionTestUtils.setField(result, "id", 1L);
    ReflectionTestUtils.setField(result, "createdAt", LocalDateTime.now());
    ReflectionTestUtils.setField(result, "updatedAt", LocalDateTime.now());

    addItems(result, request.getResponses());
    addCategories(result, request.getOption().getFortunes());

    return result;
  }

  public static FortuneResult createFortuneResultWithId(Member member) {
    return createFortuneResultWithTitleAndId(member, "2025년 월별 운세");
  }

  // Private Helper Methods
  private static SaveFortuneRequest createSaveFortuneRequest(String title) {
    FortuneOptionForm option = createValidFortuneOption(
        List.of(FortuneType.LOVE, FortuneType.HEALTH));
    List<FortuneResponse> responses = createValidFortuneResponses();

    SaveFortuneRequest request = new SaveFortuneRequest();
    request.setTitle(title);
    request.setBirthInfo(createValidBirthInfo());
    request.setOption(option);
    request.setFortuneResultYear(2025);
    request.setResponses(responses);

    return request;
  }

  private static FortuneOptionForm createValidFortuneOption(List<FortuneType> fortunes) {
    FortuneOptionForm option = new FortuneOptionForm();
    option.setAi(AIType.GEMINI);
    option.setFortunes(fortunes);
    option.setPeriod(PeriodType.MONTHLY);
    return option;
  }

  private static List<FortuneResponse> createValidFortuneResponses() {
    FortuneResponse response1 = new FortuneResponse();
    response1.setFortune(FortuneType.LOVE);
    response1.setPeriodValue(PeriodValue.JANUARY);
    response1.setResult("좋은 한 해가 될 것입니다.");

    FortuneResponse response2 = new FortuneResponse();
    response2.setFortune(FortuneType.HEALTH);
    response2.setPeriodValue(PeriodValue.FEBRUARY);
    response2.setResult("건강운이 상승합니다.");

    FortuneResponse response3 = new FortuneResponse();
    response3.setFortune(FortuneType.HEALTH);
    response3.setPeriodValue(PeriodValue.MARCH);
    response3.setResult("건강 유지를 위해 운동이 필요합니다.");

    FortuneResponse response4 = new FortuneResponse();
    response4.setFortune(FortuneType.LOVE);
    response4.setPeriodValue(PeriodValue.APRIL);
    response4.setResult("좋은 인연을 만나게 될 것입니다.");

    return List.of(response1, response2, response3, response4);
  }

  private static BirthInfoForm createValidBirthInfo() {
    BirthInfoForm birth = new BirthInfoForm();
    birth.setGender(GenderType.FEMALE);
    birth.setCalendar(CalendarType.SOLAR);
    birth.setYear(1995);
    birth.setMonth(2);
    birth.setDay(16);
    birth.setCity(CityType.SEOUL);
    birth.setTime(TimeType.TIME_11_30);
    return birth;
  }

  private static void addItems(FortuneResult result, List<FortuneResponse> responses) {
    responses.forEach(response -> {
      FortuneResultItem item = FortuneResultItem.create(response);
      result.addItem(item);
    });
  }


  private static void addCategories(FortuneResult result, List<FortuneType> types) {
    types.forEach(type -> {
      FortuneCategory category = createCategory(type);
      FortuneResultCategory resultCategory = FortuneResultCategory.create(result, category);
      result.addCategory(resultCategory);
    });
  }

  private static FortuneCategory createCategory(FortuneType type) {
    return CATEGORY_MAP.get(type);
  }
}
