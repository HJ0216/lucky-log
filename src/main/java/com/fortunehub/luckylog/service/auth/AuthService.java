package com.fortunehub.luckylog.service.auth;

import com.fortunehub.luckylog.dto.request.auth.SignupRequest;
import com.fortunehub.luckylog.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public void signup(SignupRequest request){
    if(memberRepository.existsByEmail(request.getEmail())){
      throw new IllegalArgumentException("이미 등록된 이메일입니다.");
    }
    
    String encodedPassword = passwordEncoder.encode(request.getPassword());

    memberRepository.save(request.toEntity(encodedPassword));
  }
}
