package com.fortunehub.luckylog.controller.web;

import com.fortunehub.luckylog.dto.request.fortune.FortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResult;
import com.fortunehub.luckylog.form.BirthInfoForm;
import com.fortunehub.luckylog.form.FortuneOptionForm;
import com.fortunehub.luckylog.service.fortune.GeminiService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

  private static final String AI_GEMINI = "🪂 Gemini";

  private final GeminiService geminiService;

  @GetMapping
  public String show(@ModelAttribute BirthInfoForm birthInfo, Model model) {

    model.addAttribute("fortuneOptionForm", new FortuneOptionForm());

    return "fortune-option";
  }

  @PostMapping
  public String submit(
      @Valid @ModelAttribute BirthInfoForm birthInfoForm,
      @Valid @ModelAttribute FortuneOptionForm fortuneOptionForm,
      BindingResult result,
      Model model,
      RedirectAttributes redirectAttributes
  ) {

    log.debug("운세 분석 요청 시작 - 생년: {}, 성별: {}, 운세 선택 정보: {}",
        birthInfoForm.getYear(),
        birthInfoForm.getGender(),
        fortuneOptionForm.toString());

    if (result.hasErrors()) {
      log.warn("운세 옵션 검증 실패: {}",
          result.getFieldErrors().stream()
                .map(FieldError::getField)
                .toList());

      Set<String> errorMessages = new LinkedHashSet<>();
      Set<String> errorFields = new LinkedHashSet<>();

      result.getFieldErrors().forEach(error -> {
        errorMessages.add(error.getDefaultMessage());
        errorFields.add(error.getField());
      });

      model.addAttribute("errorMessages", errorMessages);
      model.addAttribute("errorFields", errorFields);
      model.addAttribute("fortuneOptionForm", fortuneOptionForm);
      model.addAttribute("birthInfo", birthInfoForm);

      return "fortune-option";
    }

    try {
      FortuneResult fortuneResult = FortuneResult.builder().build();

      if (AI_GEMINI.equals(fortuneOptionForm.getAi())) {
        fortuneResult = geminiService.analyzeFortune(
            FortuneRequest.from(birthInfoForm, fortuneOptionForm));
      }

      // TODO: 운세별 로깅으로 전환 예정
      // TODO: overall 하드코딩 변경 예정
      log.info("운세 분석 완료 - 운세 선택 정보: {}, 응답길이: {}",
          fortuneOptionForm.getFortunes(), fortuneResult.getOverall().length());

      redirectAttributes.addFlashAttribute("fortuneResult", fortuneResult);
      redirectAttributes.addFlashAttribute("birthInfo", birthInfoForm);
      redirectAttributes.addFlashAttribute("fortuneOption", fortuneOptionForm);

      return "redirect:/fortune/result";

    } catch (Exception e) {
      log.error("사주 분석 API 호출 실패: {}", e.getMessage(), e);

      model.addAttribute("errorMessages", "사주 정보를 불러오는데 실패하였습니다.\n잠시 후 다시 시도해주세요");
      model.addAttribute("errorFields", "submit");

      model.addAttribute("fortuneOptionForm", fortuneOptionForm);
      model.addAttribute("birthInfo", birthInfoForm);

      return "fortune-option";
    }
  }

  @GetMapping("/back")
  public String backToIndex(HttpSession session, Model model) {

    BirthInfoForm savedBirthInfo = (BirthInfoForm) session.getAttribute("birthInfo");

    if (savedBirthInfo != null) {
      model.addAttribute("birthInfoForm", savedBirthInfo);
    } else {
      log.warn("뒤로가기 처리 - 세션에 저장된 데이터가 없음");
      
      model.addAttribute("birthInfoForm", new BirthInfoForm());
    }

    return "index";
  }
}
