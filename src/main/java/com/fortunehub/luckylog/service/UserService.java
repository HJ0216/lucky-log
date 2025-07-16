package com.fortunehub.luckylog.service;

import com.fortunehub.luckylog.dto.request.UserCreateRequest;
import com.fortunehub.luckylog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public void createUser(UserCreateRequest request) {
    String rawPassword = request.password();
    String encodedPassword = passwordEncoder.encode(rawPassword);

    userRepository.save(request.toEntity(encodedPassword));
  }
}
