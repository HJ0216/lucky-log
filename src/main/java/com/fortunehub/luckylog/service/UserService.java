package com.fortunehub.luckylog.service;

import com.fortunehub.luckylog.domain.User;
import com.fortunehub.luckylog.dto.request.UserCreateRequest;
import com.fortunehub.luckylog.dto.response.UserResponse;
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

  public long createUser(UserCreateRequest request) {
    String rawPassword = request.password();
    String encodedPassword = passwordEncoder.encode(rawPassword);

    User user = userRepository.save(request.toEntity(encodedPassword));
    return user.getId();
  }

  @Transactional(readOnly = true)
  public UserResponse getUser(Long id) {
    User user = userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    return UserResponse.from(user);
  }
}
