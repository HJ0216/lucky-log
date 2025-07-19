package com.fortunehub.luckylog.service;

import com.fortunehub.luckylog.domain.User;
import com.fortunehub.luckylog.dto.request.UserCreateRequest;
import com.fortunehub.luckylog.dto.request.UserNicknameUpdateRequest;
import com.fortunehub.luckylog.dto.request.UserProfileImageUpdateRequest;
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
  public boolean isEmailAvailable(String email) {
    return !userRepository.existsByEmail(email);
  }

  @Transactional(readOnly = true)
  public UserResponse getUser(Long id) {
    User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    return UserResponse.from(user);
  }

  public UserResponse updateNickname(Long id, UserNicknameUpdateRequest request) {
    User user = userRepository.findById((id)).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

    if (userRepository.existsByNickname(request.nickname())) {
      throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
    }

    user.updateNickname(request.nickname());
    return UserResponse.from(user);
  }

  public void updateProfileImage(Long id, UserProfileImageUpdateRequest request) {
    User user = userRepository.findById((id)).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    user.updateProfileImage(request.url());
  }
}
