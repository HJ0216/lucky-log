package com.fortunehub.luckylog.controller.web;

import com.fortunehub.luckylog.form.BirthInfoForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Year;

@Controller
public class IndexController {

    private static final int MIN_BIRTH_YEAR = 1940;

    @GetMapping("/")
    public String index(Model model) {
        // Model: Controller에서 생성된 데이터를 담아 View로 전달할 때 사용하는 객체

        // 빈 BirthInfoForm 객체 생성
        model.addAttribute("birthInfoForm", new BirthInfoForm());

        // 년도 범위 설정
        model.addAttribute("minYear", MIN_BIRTH_YEAR);
        model.addAttribute("maxYear", Year.now().getValue());

        return "index"; // templates/index.html 반환
    }
}
