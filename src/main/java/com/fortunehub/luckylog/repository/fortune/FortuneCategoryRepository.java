package com.fortunehub.luckylog.repository.fortune;

import com.fortunehub.luckylog.domain.fortune.FortuneCategory;
import com.fortunehub.luckylog.domain.fortune.FortuneType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FortuneCategoryRepository extends JpaRepository<FortuneCategory, Integer> {

  /**
   * SELECT fc
   * FROM FortuneCategory fc
   * WHERE fc.fortuneType IN :fortuneTypes
   */
  List<FortuneCategory> findByFortuneTypeIn(List<FortuneType> fortuneTypes);
}
