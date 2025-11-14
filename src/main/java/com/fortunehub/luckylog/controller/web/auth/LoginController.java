package com.fortunehub.luckylog.controller.web.auth;

import com.fortunehub.luckylog.controller.web.auth.form.LoginForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

  @GetMapping
  public String show(@ModelAttribute LoginForm form) {
    return "auth/login";
  }

}
