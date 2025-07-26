package com.fortunehub.luckylog.service;

import com.fortunehub.luckylog.domain.User;
import com.fortunehub.luckylog.dto.request.LoginRequest;
import com.fortunehub.luckylog.dto.request.UserCreateRequest;
import com.fortunehub.luckylog.dto.response.LoginResponse;
import com.fortunehub.luckylog.dto.response.UserResponse;
import com.fortunehub.luckylog.repository.UserRepository;
import com.fortunehub.luckylog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Transactional(readOnly = true)
  public boolean isEmailAvailable(String email) {
    return !userRepository.existsByEmail(email);
  }

  public UserResponse createUser(UserCreateRequest request) {
    String rawPassword = request.password();
    String encodedPassword = passwordEncoder.encode(rawPassword);

    User user = userRepository.save(request.toEntity(encodedPassword));
    return UserResponse.from(user);
  }

  public LoginResponse login(LoginRequest request) {
    User user = userRepository.findByEmail(request.email())
                              .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

    if(!passwordEncoder.matches(request.password(), user.getPassword())){
      throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    String accessToken = jwtUtil.createToken(user.getId());
    // token 생성에 사용된 payload만 추출 할 수 있음
    // email로만 token을 생성했다면, token으로부터 userId 정보를 가져올 수 없음

    return LoginResponse.of(accessToken, user);
  }
}
