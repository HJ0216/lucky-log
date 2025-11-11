package com.fortunehub.luckylog.service.fortune;

import com.fortunehub.luckylog.client.gemini.GeminiService;
import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.dto.request.fortune.FortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponseView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FortuneService {

  private final GeminiService geminiService;

  public List<FortuneResponseView> analyzeFortune(BirthInfoForm savedBirthInfo,
      FortuneOptionForm option) {
    switch (option.getAi()) {
      case GEMINI:
        return geminiService.analyzeFortune(FortuneRequest.from(savedBirthInfo, option));
      default:
        throw new IllegalArgumentException("Unsupported AI Type: " + option.getAi());
    }
  }
}
