package com.fortunehub.luckylog.service.fortune;

import com.fortunehub.luckylog.client.gemini.GeminiService;
import com.fortunehub.luckylog.controller.web.fortune.form.BirthInfoForm;
import com.fortunehub.luckylog.controller.web.fortune.form.FortuneOptionForm;
import com.fortunehub.luckylog.domain.fortune.FortuneCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneResult;
import com.fortunehub.luckylog.domain.fortune.FortuneResultCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneResultItem;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.domain.member.Member;
import com.fortunehub.luckylog.dto.request.fortune.FortuneRequest;
import com.fortunehub.luckylog.dto.request.fortune.SaveFortuneRequest;
import com.fortunehub.luckylog.dto.response.fortune.FortuneResponse;
import com.fortunehub.luckylog.exception.CustomException;
import com.fortunehub.luckylog.exception.ErrorCode;
import com.fortunehub.luckylog.repository.fortune.FortuneCategoryRepository;
import com.fortunehub.luckylog.repository.fortune.FortuneResultRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FortuneService {

  private static final int MAX_SAVE_COUNT = 5;

  private final GeminiService geminiService;
  private final FortuneResultRepository fortuneResultRepository;
  private final FortuneCategoryRepository fortuneCategoryRepository;

  public List<FortuneResponse> analyzeFortune(
      BirthInfoForm savedBirthInfo, FortuneOptionForm option, int fortuneResultYear) {
    return switch (option.getAi()) {
      case GEMINI -> geminiService.analyzeFortune(
          FortuneRequest.from(savedBirthInfo, option, fortuneResultYear));
      default -> throw new CustomException(ErrorCode.UNSUPPORTED_AI_TYPE);
    };
  }

  @Transactional
  public void save(Member member, SaveFortuneRequest request, BirthInfoForm birth) {
    validateBusinessRules(member, request);

    FortuneResult result = FortuneResult.create(member, request, birth);

    addItems(result, request.getResponses());
    addCategories(result, request.getOption().getFortunes());

    fortuneResultRepository.save(result);
  }

  private void addItems(FortuneResult result, List<FortuneResponse> responses) {
    responses.forEach(response -> {
      FortuneResultItem item = FortuneResultItem.create(result, response);
      result.addItem(item);
    });
  }

  private void addCategories(FortuneResult result, List<FortuneType> fortuneTypes) {
    List<FortuneCategory> categories = fortuneCategoryRepository
        .findByFortuneTypeIn(fortuneTypes);

    if (categories.size() != fortuneTypes.size()) {
      throw new IllegalArgumentException("일부 운세 카테고리를 찾을 수 없습니다.");
    }

    categories.forEach(category -> {
      FortuneResultCategory fortuneResultCategory = FortuneResultCategory.create(result, category);
      result.addCategory(fortuneResultCategory);
    });
  }

  private void validateBusinessRules(Member member, SaveFortuneRequest request) {
    if (member == null || !member.isActive()) {
      throw new IllegalArgumentException("유효하지 않은 회원입니다.");
    }

    if (isDuplicateFortuneTitle(member.getId(), request)) {
      throw new IllegalArgumentException("이미 동일한 이름의 운세가 저장되어 있습니다.");
    }

    if (isExceedMaxSaveCount(member.getId())) {
      throw new IllegalArgumentException("저장 가능한 운세 개수를 초과했습니다.");
    }

  }

  private boolean isDuplicateFortuneTitle(Long memberId, SaveFortuneRequest request) {
    return fortuneResultRepository.existsByMember_IdAndTitle(memberId, request.getTitle());
  }

  private boolean isExceedMaxSaveCount(Long memberId) {
    long count = fortuneResultRepository.countByMember_IdAndIsActiveTrue(memberId);
    return count > MAX_SAVE_COUNT;
  }
}
