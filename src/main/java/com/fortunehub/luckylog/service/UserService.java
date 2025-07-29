package com.fortunehub.luckylog.service;

import com.fortunehub.luckylog.domain.User;
import com.fortunehub.luckylog.dto.request.user.UserNicknameUpdateRequest;
import com.fortunehub.luckylog.dto.request.user.UserProfileImageUpdateRequest;
import com.fortunehub.luckylog.dto.response.user.UserResponse;
import com.fortunehub.luckylog.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public UserResponse getUser(Long id) {
    User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    return UserResponse.from(user);
  }

  public List<UserResponse> getAllUsers() {
    List<User> users = userRepository.findAll();
    return users.stream()
                .map(UserResponse::from)
                .toList();
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

  public void deleteProfileImage(Long id) {
    User user = userRepository.findById((id)).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    user.updateProfileImage(null);
  }

  public void deleteUser(Long id) {
    User user = userRepository.findById((id)).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    user.updateIsActive(false);
  }
}
