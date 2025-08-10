package com.fortunehub.luckylog.controller.web;

import com.fortunehub.luckylog.form.BirthInfoForm;
import jakarta.validation.Valid;
import java.time.Year;
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
@RequestMapping("/")
public class IndexController {

  private static final int MIN_BIRTH_YEAR = 1940;

  @GetMapping
  public String index(Model model) {
    // Model: Controller에서 생성된 데이터를 담아 View로 전달할 때 사용하는 객체

    log.info("메인 페이지 접근");

    // 빈 BirthInfoForm 객체 생성
    model.addAttribute("birthInfoForm", new BirthInfoForm());

    // 년도 범위 설정
    model.addAttribute("minYear", MIN_BIRTH_YEAR);
    model.addAttribute("maxYear", Year.now().getValue());

    return "index"; // templates/index.html 반환
  }

  @PostMapping
  public String submit(
      @Valid @ModelAttribute BirthInfoForm birthInfoForm,
      BindingResult result,
      Model model,
      RedirectAttributes redirectAttributes
  ) throws Exception {

    log.debug("생년월일 제출: {}", birthInfoForm.toString());

    if (result.hasErrors()) {
      log.warn("생년월일 검증 실패: {}",
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

      return "index";
    }

    log.info("생년월일 검증 완료 - 운세 선택 페이지로 이동");

    redirectAttributes.addFlashAttribute("birthInfo", birthInfoForm);

    return "redirect:/fortune/option";
  }
}
