package com.fortunehub.luckylog.controller.web;

import com.fortunehub.luckylog.controller.web.form.SignupForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/signup")
public class SignUpController {

  @GetMapping
  public String show(@ModelAttribute SignupForm form) {
    // @ModelAttribute는 넘어오는 데이터가 없어도 자동으로 빈 객체를 생성
    // 매개변수 이름과 무관하게 form 객체 이름 사용
    return "signup";
  }
}
