package com.fortunehub.luckylog.controller.web.fortune;

import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.domain.common.LoadingMessage;
import com.fortunehub.luckylog.domain.fortune.AIType;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.fortune.PeriodType;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.service.fortune.FortuneService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/fortune/option")
public class FortuneOptionController {

  private static final String FORTUNE_OPTION_VIEW = "fortune/fortune-option";
  private static final String REDIRECT_HOME = "redirect:/";
  private static final String REDIRECT_FORTUNE_RESULT = "redirect:/fortune/result";

  private final FortuneService fortuneService;

  @ModelAttribute("aiTypes")
  public List<AIType> aiTypes() {
    return AIType.ALL_TYPES;
  }

  @ModelAttribute("fortuneTypes")
  public List<FortuneType> fortuneTypes() {
    return FortuneType.ALL_TYPES;
  }

  @ModelAttribute("periodTypes")
  public List<PeriodType> periodTypes() {
    return PeriodType.ALL_TYPES;
  }

  @ModelAttribute("loadingMessage")
  public String loadingMessage() {
    return LoadingMessage.getRandomMessage();
  }

  @GetMapping
  public String show(@ModelAttribute FortuneOptionForm fortuneOptionForm) {
    // @ModelAttribute는 넘어오는 데이터가 없어도 자동으로 빈 객체를 생성

    return FORTUNE_OPTION_VIEW;
  }

  @PostMapping
  public String submit(
      @Valid @ModelAttribute FortuneOptionForm option,
      BindingResult result,
      @SessionAttribute(name = "birthInfo", required = false) BirthInfoForm savedBirthInfo,
      RedirectAttributes redirectAttributes
  ) {

    if (savedBirthInfo == null) {
      log.warn("[운세 옵션 검증 실패] - [세션 데이터 누락] | 생년월일 정보가 세션에 저장되지 않음");
      redirectAttributes.addFlashAttribute("errorMessage", "생년월일이 입력되지 않았습니다. 처음부터 다시 진행해주세요.");
      return REDIRECT_HOME;
    }

    if (result.hasErrors()) {
      result.getFieldErrors().forEach(error ->
          log.warn(
              "[운세 옵션 검증 실패] - [입력값 유효성 오류] | field={} | rejectedValue={} | message={}",
              error.getField(), error.getRejectedValue(), error.getDefaultMessage())
      );

      return FORTUNE_OPTION_VIEW;
    }

    try {
      int fortuneResultYear = LocalDateTime.now().getYear();
      List<FortuneResponse> responses = fortuneService.generateFortune(
          savedBirthInfo, option, fortuneResultYear);
      redirectAttributes.addFlashAttribute("fortuneResultYear", fortuneResultYear);
      redirectAttributes.addFlashAttribute("option", option); //자동으로 Model에 포함
      redirectAttributes.addFlashAttribute("responses", responses);

      return REDIRECT_FORTUNE_RESULT;

    } catch (CustomException e) {
      result.addError(
          new ObjectError(result.getObjectName(), "😲 사주 정보를 불러오는데 실패하였습니다.\n잠시 후 다시 시도해주세요."));
      // result.getObjectName(): 동적으로 폼 이름을 가져와 어떤 객체의 에러인지 지정
      // 어떤 객체의 에러인지 지정(페이지에 폼이 2개 이상일 수 있음)
      // 생략하면 @ModelAttribute의 클래스명의 camelCase가 자동으로 이름이 됨

      return FORTUNE_OPTION_VIEW;
    } catch (Exception e) {
      log.error("[운세 분석 실패] - [API 호출 오류] | option={} | message={}",
          option, e.getMessage(), e);

      result.addError(
          new ObjectError(result.getObjectName(), "😲 사주 정보를 불러오는데 실패하였습니다.\n잠시 후 다시 시도해주세요."));

      return FORTUNE_OPTION_VIEW;
    }
  }

  @GetMapping("/back")
  public String backToIndex() {
    return REDIRECT_HOME;
  }
}
