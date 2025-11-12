package com.fortunehub.luckylog.service.fortune;

import com.fortunehub.luckylog.client.gemini.GeminiService;
import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.dto.request.fortune.FortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponseView;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FortuneService {

  private final GeminiService geminiService;

  public List<FortuneResponseView> analyzeFortune(
      BirthInfoForm savedBirthInfo, FortuneOptionForm option) {
    return switch (option.getAi()){
      case GEMINI -> geminiService.analyzeFortune(FortuneRequest.from(savedBirthInfo, option));
      default -> throw new CustomException(ErrorCode.UNSUPPORTED_AI_TYPE);
    };
  }
}
