package com.fortunehub.luckylog.repository;

import com.fortunehub.luckylog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  // select * from users where email=:email
  boolean existsByEmail(String email);

  // select * from users where nickname = :nickname
  boolean existsByNickname(String nickname);
}
