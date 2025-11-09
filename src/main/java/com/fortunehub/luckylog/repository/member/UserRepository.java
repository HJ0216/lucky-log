package com.fortunehub.luckylog.repository.member;

import com.fortunehub.luckylog.domain.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Member, Long> {

  // select * from users where email=:email
  boolean existsByEmail(String email);

  // select * from users where nickname = :nickname
  boolean existsByNickname(String nickname);

  // select * from users where email = :email
  Optional<Member> findByEmail(String mail);
}
