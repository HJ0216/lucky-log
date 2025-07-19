package com.fortunehub.luckylog.controller;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortunehub.luckylog.domain.User;
import com.fortunehub.luckylog.dto.request.UserCreateRequest;
import com.fortunehub.luckylog.dto.request.UserNicknameUpdateRequest;
import com.fortunehub.luckylog.dto.request.UserProfileImageUpdateRequest;
import com.fortunehub.luckylog.repository.UserRepository;
import com.fortunehub.luckylog.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc // Bean을 찾을 수 없음
@Transactional
class UserControllerIntegrationTest {

  private static final String BASE_URL = "/api/v1/users";

  @Autowired
  private MockMvc mockMvc;
  // perform() 메서드를 통해 가상의 HTTP 요청을 보냄

  @Autowired
  private ObjectMapper objectMapper;
  // 자바 객체를 JSON 문자열로 변환 또는 JSON 문자열을 자바 객체로 변환

  @Autowired
  UserService userService;

  @Autowired
  UserRepository userRepository;

  // ========== 이메일 중복 검사 API 테스트 ==========
  @Test
  @DisplayName("이메일 중복 검사 - 사용 가능")
  void checkEmailDuplicate_AvailableEmail_ReturnsOk() throws Exception {
    // given
    String email = "new@email.com";

    // when & then
    mockMvc.perform((get(BASE_URL + "/check-email")
               .param("email", email)))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.available").value(true))
           .andExpect(jsonPath("$.message").value("사용 가능한 이메일입니다."));
  }

  @Test
  @DisplayName("이메일 중복 검사 - 중복 이메일")
  void checkEmailDuplicate_DuplicateEmail_ReturnsConflict() throws Exception {
    // given
    String email = "duplicate@email.com";
    User user = new User(email, "user", "password123");
    userRepository.save(user);

    // when & then
    mockMvc.perform(get(BASE_URL + "/check-email")
               .param("email", email))
           .andDo(print())
           .andExpect(status().isConflict())
           .andExpect(jsonPath("$.available").value(false))
           .andExpect(jsonPath("$.message").value("이미 사용중인 이메일입니다."));
  }

  @Test
  @DisplayName("이메일 중복 검사 - 파라미터 누락")
  void checkEmailDuplicate_MissingEmail_ReturnsBadRequest() throws Exception {
    mockMvc.perform(get(BASE_URL + "/check-email"))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("MISSING_REQUIRED_PARAMETER"))
           .andExpect(jsonPath("$.message").value(containsString("필수 파라미터(email)가 누락되었습니다.")));
  }


  // ========== 회원 가입 API 테스트 ==========
  @Test
  @DisplayName("회원 가입 - 성공")
  void createUser_ValidRequest_ReturnsCreated() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@example.com",
        "test",
        "password123"
    );

    // when & then
    mockMvc.perform(post(BASE_URL)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isCreated());

    // 실제 DB에 저장되었는지 확인
    User savedUser = userRepository.findByEmail("test@example.com").orElseThrow();
    assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    assertThat(savedUser.getNickname()).isEqualTo("test");
  }

  @Test
  @DisplayName("회원 가입 - 이메일 필드 누락")
  void createUser_EmptyEmail_ReturnsBadRequest() throws Exception {
    UserCreateRequest request = new UserCreateRequest(
        "",
        "test",
        "password123"
    );

    mockMvc.perform(post(BASE_URL)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("이메일은 필수입니다.")));
  }

  @Test
  @DisplayName("회원 가입 - 잘못된 이메일 형식")
  void createUser_InvalidEmailFormat_ReturnsBadRequest() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "invalid-email", // 잘못된 이메일 형식
        "testuser",
        "password123"
    );

    // when & then
    mockMvc.perform(post(BASE_URL)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("올바른 이메일 형식이 아닙니다.")));
  }

  @Test
  @DisplayName("회원 가입 - 이메일 길이 초과")
  void createUser_EmailTooLong_ReturnsBadRequest() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "verylongemailaddressthatexceedsfiftycharacters@example.com", // 50자 초과
        "testuser",
        "password123"
    );

    // when & then
    mockMvc.perform(post(BASE_URL)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("이메일은 50자를 초과할 수 없습니다.")));
  }

  @Test
  @DisplayName("회원 가입 - 닉네임 필드 누락")
  void createUser_EmptyNickname_ReturnsBadRequest() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@example.com",
        "", // 빈 닉네임
        "password123"
    );

    // when & then
    mockMvc.perform(post(BASE_URL)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("닉네임은 필수입니다.")))
           .andExpect(jsonPath("$.message").value(containsString("닉네임은 2자 이상 8자 이하여야 합니다.")));
  }

  @Test
  @DisplayName("회원 가입 - 닉네임 길이 부족")
  void createUser_NicknameTooShort_ReturnsBadRequest() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@example.com",
        "a", // 1자 (2자 미만)
        "password123"
    );

    // when & then
    mockMvc.perform(post(BASE_URL)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("닉네임은 2자 이상 8자 이하여야 합니다.")));
  }

  @Test
  @DisplayName("회원 가입 - 닉네임 길이 초과")
  void createUser_NicknameTooLong_ReturnsBadRequest() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@example.com",
        "verylongnickname", // 8자 초과
        "password123"
    );

    // when & then
    mockMvc.perform(post(BASE_URL)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("닉네임은 2자 이상 8자 이하여야 합니다.")));
  }

  @Test
  @DisplayName("회원 가입 - 비밀번호 필드 누락")
  void createUser_EmptyPassword_ReturnsBadRequest() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@example.com",
        "testuser",
        ""
    );

    // when & then
    mockMvc.perform(post(BASE_URL)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value(containsString("비밀번호는 필수입니다.")))
           .andExpect(jsonPath("$.message").value(containsString("비밀번호는 8자 이상 20자 이하여야 합니다.")));
  }

  @Test
  @DisplayName("회원 가입 - 비밀번호 길이 부족")
  void createUser_PasswordTooShort_ReturnsBadRequest() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@example.com",
        "testuser",
        "1234567" // 7자 (8자 미만)
    );

    // when & then
    mockMvc.perform(post(BASE_URL)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.message").value("password: 비밀번호는 8자 이상 20자 이하여야 합니다."));
  }


  // ========== 회원 조회 API 테스트 ==========
  @Test
  @DisplayName("회원 조회 - 성공")
  void getUser_ValidId_ReturnsOk() throws Exception {
    // given
    User user = new User("test@email.com", "test", "password123");
    User savedUser = userRepository.save(user);

    // when & then
    mockMvc.perform(get(BASE_URL + "/{id}", savedUser.getId()))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.email").value("test@email.com"))
           .andExpect(jsonPath("$.nickname").value("test"));
  }

  @Test
  @DisplayName("회원 조회 - 존재하지 않는 회원")
  void getUser_UserNotFound_ReturnsBadRequest() throws Exception {
    // given
    Long nonExistentUserId = 1L;

    // when & then
    mockMvc.perform(get(BASE_URL + "/{id}", nonExistentUserId))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
           .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."));
  }

  // ========== 닉네임 변경 API 테스트 ==========
  @Test
  @DisplayName("닉네임 변경 - 성공")
  void updateNickname_ValidNickname_ReturnsOk() throws Exception {
    // given
    User user = new User("test@email.com", "test", "password123");
    User savedUser = userRepository.save(user);

    UserNicknameUpdateRequest request = new UserNicknameUpdateRequest("test-new");

    // when & then
    mockMvc.perform(patch(BASE_URL + "/{id}/nickname", savedUser.getId())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.email").value("test@email.com"))
           .andExpect(jsonPath("$.nickname").value("test-new"));

    // 실제 DB에서 변경되었는지 확인
    User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
    assertThat(updatedUser.getNickname()).isEqualTo("test-new");
  }

  @Test
  @DisplayName("닉네임 변경 - 존재하지 않는 회원")
  void updateNickname_UserNotFound_ReturnsBadRequest() throws Exception {
    // given
    Long nonExistentUserId = 1L;
    UserNicknameUpdateRequest request = new UserNicknameUpdateRequest("test-new");

    // when & then
    mockMvc.perform(patch(BASE_URL + "/{id}/nickname", nonExistentUserId)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
           .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."));
  }

  @Test
  @DisplayName("닉네임 변경 - 중복 닉네임")
  void updateNickname_DuplicateNickname_ReturnsBadRequest() throws Exception {
    // given
    User user1 = new User("test@email.com", "test", "password123");
    User user2 = new User("test2@email.com", "test2", "password123");
    User savedUser1 = userRepository.save(user1);
    userRepository.save(user2);

    UserNicknameUpdateRequest request = new UserNicknameUpdateRequest("test");

    // when & then
    mockMvc.perform(patch(BASE_URL + "/{id}/nickname", savedUser1.getId())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
           .andExpect(jsonPath("$.message").value("이미 사용중인 닉네임입니다."));
  }

  @Test
  @DisplayName("닉네임 변경 - RequestBody 누락")
  void updateNickname_MissingRequestBody_ReturnsBadRequest() throws Exception {
    // given
    User user = new User("test@email.com", "test", "password123");
    User savedUser = userRepository.save(user);

    // when & then
    mockMvc.perform(patch(BASE_URL + "/{id}/nickname", savedUser.getId())
               .contentType(MediaType.APPLICATION_JSON))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("INVALID_REQUEST_BODY"))
           .andExpect(jsonPath("$.message").value("요청 본문이 누락되었거나 형식이 올바르지 않습니다."));
  }


  // ========== 프로필 이미지 변경 API 테스트 ==========
  @Test
  @DisplayName("프로필 이미지 변경 - 성공")
  void updateProfileImage_ValidImage_ReturnsNoContent() throws Exception{
    // given
    User user = new User("test@email.com", "test", "password123");
    User savedUser = userRepository.save(user);

    String url = "https://example.com/images/profile/test-user.jpg";
    UserProfileImageUpdateRequest request = new UserProfileImageUpdateRequest(url);

    // when & then
    mockMvc.perform(patch(BASE_URL + "/{id}/profile-image", savedUser.getId())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isNoContent());

    // 실제 DB에서 변경되었는지 확인
    User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
    assertThat(updatedUser.getProfileImageUrl()).isEqualTo(url);
  }

  @Test
  @DisplayName("프로필 이미지 제거 - 성공")
  void updateProfileImage_NullImage_RemovesImage() throws Exception {
    // given - 기존에 프로필 이미지가 있는 사용자
    User user = new User("test@email.com", "test", "password123");
    User savedUser = userRepository.save(user);

    UserProfileImageUpdateRequest request = new UserProfileImageUpdateRequest(null);

    // when & then
    mockMvc.perform(patch(BASE_URL + "/{id}/profile-image", savedUser.getId())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isNoContent());

    // 실제 DB에서 제거되었는지 확인
    User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
    assertThat(updatedUser.getProfileImageUrl()).isNull();
  }

  @Test
  @DisplayName("프로필 이미지 변경 - 존재하지 않는 회원")
  void updateProfileImage_UserNotFound_ReturnsBadRequest() throws Exception {
    // given
    Long userId = 999L;
    UserProfileImageUpdateRequest request = new UserProfileImageUpdateRequest(
        "https://example.com/images/profile/test.jpg"
    );

    // when & then
    mockMvc.perform(patch(BASE_URL + "/{id}/profile-image", userId)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
           .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."));
  }


  // ========== 회원 삭제 API 테스트 ==========
  @Test
  @DisplayName("사용자 삭제 - 소프트 삭제 확인")
  void deleteUser_SoftDelete_MarksAsInactive() throws Exception {
    // given
    User user = new User("test@email.com", "test", "password123");
    User savedUser = userRepository.save(user);
    assertThat(savedUser.isActive()).isTrue(); // 초기에는 활성 상태

    // when
    mockMvc.perform(delete(BASE_URL + "/{id}", savedUser.getId()))
           .andDo(print())
           .andExpect(status().isNoContent());

    // then - 소프트 삭제의 경우 (isActive = false로 변경)
    User deletedUser = userRepository.findById(savedUser.getId()).orElseThrow();
    assertThat(deletedUser.isActive()).isFalse();
  }

  @Test
  @DisplayName("사용자 삭제 - 존재하지 않는 사용자")
  void deleteUser_UserNotFound_ReturnsBadRequest() throws Exception {
    // given
    Long userId = 999L;

    // when & then
    mockMvc.perform(delete(BASE_URL + "/{id}", userId))
           .andDo(print())
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
           .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."));
  }
  
  
  // ========== 모든 회원 조회 API 테스트 ==========
  @Test
  @DisplayName("모든 사용자 조회 - 성공")
  void getAllUsers_Success_ReturnsUserList() throws Exception {
    // given
    User user1 = new User("user1@email.com", "user1", "password123");
    User user2 = new User("user2@email.com", "user2", "password456");
    User user3 = new User("user3@email.com", "user3", "password789");

    userRepository.saveAll(List.of(user1, user2, user3));

    // when & then
    mockMvc.perform(get(BASE_URL))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$", hasSize(3)))
           .andExpect(jsonPath("$[0].email", is("user1@email.com")))
           .andExpect(jsonPath("$[0].nickname", is("user1")))
           .andExpect(jsonPath("$[1].email", is("user2@email.com")))
           .andExpect(jsonPath("$[1].nickname", is("user2")))
           .andExpect(jsonPath("$[2].email", is("user3@email.com")))
           .andExpect(jsonPath("$[2].nickname", is("user3")));
  }

  @Test
  @DisplayName("모든 사용자 조회 - 빈 목록")
  void getAllUsers_EmptyList_ReturnsEmptyArray() throws Exception {
    // given - 데이터베이스가 비어있는 상태

    // when & then
    mockMvc.perform(get(BASE_URL))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$", hasSize(0)))
           .andExpect(jsonPath("$", is(emptyList())));
  }

  @Test
  @DisplayName("모든 사용자 조회 - 대량 데이터")
  void getAllUsers_LargeDataSet_ReturnsAllUsers() throws Exception {
    // given - 100명의 사용자 생성
    List<User> users = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      users.add(new User("user" + i + "@email.com", "user" + i, "password"));
    }
    userRepository.saveAll(users);

    // when & then
    mockMvc.perform(get(BASE_URL))
           .andDo(print())
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$", hasSize(100)))
           .andExpect(jsonPath("$[0].email", is("user1@email.com")))
           .andExpect(jsonPath("$[99].email", is("user100@email.com")));
  }
}
