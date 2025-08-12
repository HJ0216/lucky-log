package com.fortunehub.luckylog.controller.web;

import com.fortunehub.luckylog.dto.response.fortune.FortuneResult;
import com.fortunehub.luckylog.form.BirthInfoForm;
import com.fortunehub.luckylog.form.FortuneOptionForm;
import jakarta.validation.Valid;
import java.util.LinkedHashSet;
import java.util.Set;
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
@Controller
@RequestMapping("/fortune/option")
public class FortuneOptionController {

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
  ) throws Exception {

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

    log.info("운세 옵션 검증 완료 - 운세 결과 페이지로 이동");

    redirectAttributes.addFlashAttribute("birthInfo", birthInfoForm);
    redirectAttributes.addFlashAttribute("fortuneOption", fortuneOptionForm);
    // TODO: ai 연결
    redirectAttributes.addFlashAttribute("fortuneResult", new FortuneResult());

    return "redirect:/fortune/result";
  }
}
