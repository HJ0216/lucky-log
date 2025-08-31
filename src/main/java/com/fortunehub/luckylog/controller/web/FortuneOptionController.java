package com.fortunehub.luckylog.controller.web;

import com.fortunehub.luckylog.domain.fortune.AIType;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.fortune.PeriodType;
import com.fortunehub.luckylog.dto.request.fortune.FortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponseView;
import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.service.fortune.GeminiService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/fortune/option")
public class FortuneOptionController {

  private final GeminiService geminiService;

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

  @GetMapping
  public String show(@ModelAttribute FortuneOptionForm fortuneOptionForm) {
    // @ModelAttribute는 넘어오는 데이터가 없어도 자동으로 빈 객체를 생성

    return "fortune-option";
  }

  @PostMapping
  public String submit(
      @Valid @ModelAttribute FortuneOptionForm option,
      BindingResult result,
      HttpSession session,
      RedirectAttributes redirectAttributes
  ) {

    BirthInfoForm savedBirthInfo = (BirthInfoForm) session.getAttribute("birthInfo");

    log.debug("운세 분석 요청 시작 - 생년: {}, 성별: {}, 운세 선택 정보: {}",
        savedBirthInfo.getYear(),
        savedBirthInfo.getGender(),
        option.toString());

    if (result.hasErrors()) {
      result.getFieldErrors().forEach(error ->
          log.debug("운세 옵션 검증 실패: 필드: {}, 입력값: {}, 메시지: {}",
              error.getField(),
              error.getRejectedValue(),
              error.getDefaultMessage())
      );

      return "fortune-option";
    }

    try {
      List<FortuneResponseView> responses = null;

      if (option.getAi() == AIType.GEMINI) {
        responses = geminiService.analyzeFortune(
            FortuneRequest.from(savedBirthInfo, option));
      }

      redirectAttributes.addFlashAttribute("option", option); //자동으로 Model에 포함
      redirectAttributes.addFlashAttribute("response", responses);

      return "redirect:/fortune/result";

    } catch (Exception e) {
      log.error("사주 분석 API 호출 실패: {}", e.getMessage(), e);

      result.addError(
          new ObjectError("FortuneOptionForm", "사주 정보를 불러오는데 실패하였습니다.\n잠시 후 다시 시도해주세요"));
      // @ModelAttribute로 선언된 객체(FortuneOptionForm)에만 사용

      return "fortune-option";
    }
  }

  @GetMapping("/back")
  public String backToIndex() {
    return "redirect:/";
  }
}
