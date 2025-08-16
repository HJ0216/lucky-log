package com.fortunehub.luckylog.controller.web;

import com.fortunehub.luckylog.dto.request.fortune.FortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResult;
import com.fortunehub.luckylog.form.BirthInfoForm;
import com.fortunehub.luckylog.form.FortuneOptionForm;
import com.fortunehub.luckylog.service.fortune.GeminiService;
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

    log.info("운세 선택 페이지 접근");

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

    log.debug("운세 옵션 제출 - 생년월일 정보: {}", birthInfoForm.toString());
    log.debug("운세 옵션 제출 - 운세 선택 정보: {}", fortuneOptionForm.toString());

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
      redirectAttributes.addFlashAttribute("fortuneResult", fortuneResult);

    } catch (Exception e) {
      log.error("사주 분석 API 호출 실패: {}", e);

      model.addAttribute("errorMessages", "사주 정보를 불러오는데 실패하였습니다.\n잠시 후 다시 시도해주세요");
      model.addAttribute("errorFields", "submit");

      model.addAttribute("fortuneOptionForm", fortuneOptionForm);
      model.addAttribute("birthInfo", birthInfoForm);

      return "fortune-option";
    }

    log.info("운세 옵션 검증 완료 - 운세 결과 페이지로 이동");

    redirectAttributes.addFlashAttribute("birthInfo", birthInfoForm);
    redirectAttributes.addFlashAttribute("fortuneOption", fortuneOptionForm);

    return "redirect:/fortune/result";
  }
}
