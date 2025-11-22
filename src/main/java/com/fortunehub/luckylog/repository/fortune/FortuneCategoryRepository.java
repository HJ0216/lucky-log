package com.fortunehub.luckylog.repository.fortune;

import com.fortunehub.luckylog.domain.fortune.FortuneCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FortuneCategoryRepository extends JpaRepository<FortuneCategory, Integer> {

}
