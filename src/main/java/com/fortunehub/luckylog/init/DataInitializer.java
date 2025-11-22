package com.fortunehub.luckylog.init;

import com.fortunehub.luckylog.domain.fortune.FortuneCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import com.fortunehub.luckylog.repository.fortune.FortuneCategoryRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class DataInitializer {

  private final FortuneCategoryRepository repository;

  @PostConstruct
  @Transactional
  public void init() {
    if (repository.count() > 0) return;

    repository.saveAll(List.of(
        FortuneCategory.create(FortuneType.OVERALL),
        FortuneCategory.create(FortuneType.MONEY),
        FortuneCategory.create(FortuneType.LOVE),
        FortuneCategory.create(FortuneType.CAREER),
        FortuneCategory.create(FortuneType.STUDY),
        FortuneCategory.create(FortuneType.LUCK),
        FortuneCategory.create(FortuneType.FAMILY),
        FortuneCategory.create(FortuneType.HEALTH)
    ));
  }
}
