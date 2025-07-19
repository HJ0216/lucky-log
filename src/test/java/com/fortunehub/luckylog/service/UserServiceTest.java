package com.fortunehub.luckylog.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fortunehub.luckylog.domain.User;
import com.fortunehub.luckylog.dto.request.UserCreateRequest;
import com.fortunehub.luckylog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserServiceTest {

  @Autowired
  UserService userService;

  @Autowired
  UserRepository userRepository;

  @Test
  @DisplayName("회원 가입 - 성공")
  void createUser_ValidRequest_ReturnsUserId() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@example.com",
        "test",
        "password123"
    );

    // when
    Long savedId = userService.createUser(request);

    // then
    User user = userRepository.findById(savedId).orElseThrow();
    assertThat(user.getEmail()).isEqualTo(request.email());
    assertThat(user.getNickname()).isEqualTo(request.nickname());
  }

  @Test
  @DisplayName("이메일 중복 검사 - 사용 가능")
  void isEmailAvailable_NewEmail_ReturnsTrue() throws Exception {
    // given
    String email = "new@example.com";

    // when
    boolean available = userService.isEmailAvailable(email);

    // then
    assertThat(available).isTrue();
  }

  @Test
  @DisplayName("이메일 중복 검사 - 사용 불가능")
  void isEmailAvailable_DuplicateEmail_ReturnsFalse() throws Exception {
    // given
    String email = "test@example.com";
    UserCreateRequest request = new UserCreateRequest(
        email,
        "test",
        "password123"
    );

    // when
    userService.createUser(request);
    boolean available = userService.isEmailAvailable(email);

    // then
    assertThat(available).isFalse();
  }

}