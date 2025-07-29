package com.fortunehub.luckylog.controller;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortunehub.luckylog.domain.User;
import com.fortunehub.luckylog.dto.request.user.UserNicknameUpdateRequest;
import com.fortunehub.luckylog.dto.request.user.UserProfileImageUpdateRequest;
import com.fortunehub.luckylog.repository.UserRepository;
import com.fortunehub.luckylog.service.UserService;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc // Bean을 찾을 수 없음
@Transactional
@DisplayName("UserController 클래스")
class UserControllerIntegrationTest {

  private static final String BASE_URL = "/api/v1/users";

  private static final Long NON_EXISTING_ID = 999L;
  private static final String VALID_EMAIL = "valid@email.com";
  private static final String VALID_NICKNAME = "john";
  private static final String UPDATED_VALID_NICKNAME = "johny";
  private static final String VALID_PASSWORD = "password123";
  private static final String VALID_PROFILE_IMAGE = "https://example.com/images/profile/test-user.jpg";


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

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Nested
  @DisplayName("getUser 메서드는")
  class Describe_getUser {

    @Nested
    @DisplayName("만약 가입된 회원을 조회하면")
    class Context_with_existing_user {

      @Test
      @DisplayName("회원 정보를 반환한다")
      void it_returns_user_details() throws Exception {
        // given
        User user = createUser(VALID_EMAIL, VALID_NICKNAME, passwordEncoder.encode(VALID_PASSWORD));
        userRepository.save(user);

        // when
        ResultActions result = performGetUser(user.getId());

        // then
        result.andDo(print())
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$.id").value(user.getId()))
              .andExpect(jsonPath("$.email").value(user.getEmail()))
              .andExpect(jsonPath("$.password").doesNotExist())
              .andExpect(jsonPath("$.nickname").value(user.getNickname()))
              .andExpect(jsonPath("$.profileImageUrl").isEmpty())
              .andExpect(jsonPath("$.createdAt").isNotEmpty());
      }
    }

    @Nested
    @DisplayName("만약 가입하지 않은 회원을 조회하면")
    class Context_with_non_existing_user {

      @Test
      @DisplayName("존재하지 않는 회원이라는 응답을 반환한다")
      void it_returns_user_not_found_error() throws Exception {
        // given
        Long userId = NON_EXISTING_ID;

        // when
        ResultActions result = performGetUser(userId);

        // then
        result.andDo(print())
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
              .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."));

      }
    }

    private ResultActions performGetUser(Long userId) throws Exception {
      return mockMvc.perform(get(BASE_URL + "/{id}", userId));
    }
  }

  @Nested
  @DisplayName("getAllUsers 메서드는")
  class Describe_getAllUsers {

    @Nested
    @DisplayName("만약 유효한 조회 요청이라면")
    class Context_with_valid_request {

      @Test
      @DisplayName("모든 회원 정보를 반환한다")
      void it_returns_all_user_details() throws Exception {
        // given
        List<User> users = IntStream.rangeClosed(1, 3)
                                    .mapToObj(i -> createUser(
                                        i + VALID_EMAIL,
                                        i + VALID_NICKNAME,
                                        passwordEncoder.encode(VALID_PASSWORD)
                                    ))
                                    .toList();
        userRepository.saveAll(users);

        // when
        ResultActions result = performGetAllUsers();

        // then
        result.andDo(print())
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$", hasSize(users.size())))
              .andExpect(jsonPath("$[*].id", containsInAnyOrder(
                  // JSON에서 숫자는 기본적으로 Integer로 파싱됨
                  // DB에서 가져온 ID (Long) vs JSON 파싱된 값 (Integer) 비교 시 타입 불일치
                  users.get(0).getId().intValue(),
                  users.get(1).getId().intValue(),
                  users.get(2).getId().intValue()
              )))
              .andExpect(jsonPath("$[*].email", containsInAnyOrder(
                  users.get(0).getEmail(),
                  users.get(1).getEmail(),
                  users.get(2).getEmail()
              )))
              .andExpect(jsonPath("$[*].nickname", containsInAnyOrder(
                  users.get(0).getNickname(),
                  users.get(1).getNickname(),
                  users.get(2).getNickname()
              )))
              .andExpect(jsonPath("$[*].password").doesNotExist());
      }
    }

    @Nested
    @DisplayName("만약 회원이 없다면")
    class Context_with_no_existing_user {

      @Test
      @DisplayName("빈 배열을 반환한다")
      void it_returns_empty_list() throws Exception {
        // given
        // 데이터베이스가 비어있는 상태

        // when
        ResultActions result = performGetAllUsers();

        // then
        result.andDo(print())
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$", hasSize(0)))
              .andExpect(jsonPath("$", is(emptyList())));
      }
    }

    private ResultActions performGetAllUsers() throws Exception {
      return mockMvc.perform(get(BASE_URL));
    }
  }

  @Nested
  @DisplayName("updateNickname 메서드는")
  class Describe_updateNickname {

    @Nested
    @DisplayName("만약 유효한 닉네임 변경 요청이라면")
    class Context_with_valid_nickname_request {

      @Test
      @DisplayName("닉네임을 변경한다")
      void it_updates_nickname_successfully() throws Exception {
        // given
        User user = createUser(VALID_EMAIL, VALID_NICKNAME, passwordEncoder.encode(VALID_PASSWORD));
        userRepository.save(user);

        UserNicknameUpdateRequest request = new UserNicknameUpdateRequest(UPDATED_VALID_NICKNAME);

        // when
        ResultActions result = performUpdateNickname(user.getId(), request);

        // then
        result.andDo(print())
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$.id").value(user.getId()))
              .andExpect(jsonPath("$.email").value(user.getEmail()))
              .andExpect(jsonPath("$.nickname").value(request.nickname()))
              .andExpect(jsonPath("$.password").doesNotExist());

        // 실제 DB에서 변경되었는지 확인
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getNickname()).isEqualTo(request.nickname());
      }
    }

    @Nested
    @DisplayName("만약 존재하지 않는 사용자라면")
    class Context_with_non_existing_user_id {

      @Test
      @DisplayName("존재하지 않는 사용자라는 응답을 반환한다")
      void it_returns_user_not_found_error() throws Exception {
        // given
        Long userId = NON_EXISTING_ID;
        UserNicknameUpdateRequest request = new UserNicknameUpdateRequest(UPDATED_VALID_NICKNAME);

        // when
        ResultActions result = performUpdateNickname(userId, request);

        // then
        result.andDo(print())
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
              .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."));
      }
    }

    @Nested
    @DisplayName("만약 이미 사용중인 닉네임이라면")
    class Context_with_used_nickname {

      @Test
      @DisplayName("사용 불가능하다는 응답을 반환한다")
      void it_returns_duplicate_nickname_error() throws Exception {
        // given
        List<User> users = IntStream.rangeClosed(1, 2)
                                    .mapToObj(i -> createUser(
                                        i + VALID_EMAIL,
                                        i + VALID_NICKNAME,
                                        passwordEncoder.encode(VALID_PASSWORD)
                                    ))
                                    .toList();
        userRepository.saveAll(users);

        UserNicknameUpdateRequest request = new UserNicknameUpdateRequest(1 + VALID_NICKNAME);

        // when
        ResultActions result = performUpdateNickname(users.get(1).getId(), request);

        // then
        result.andDo(print())
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
              .andExpect(jsonPath("$.message").value("이미 사용중인 닉네임입니다."));
      }
    }

    @Nested
    @DisplayName("만약 요청 형식이 잘못되면")
    class Context_invalid_request_format {

      @Test
      @DisplayName("요청 형식이 올바르지 않다는 응답을 반환한다")
      void it_returns_missing_request_error() throws Exception {
        // given
        User user = createUser(VALID_EMAIL, VALID_NICKNAME, passwordEncoder.encode(VALID_PASSWORD));
        userRepository.save(user);

        // when
        ResultActions result = performUpdateNickname(user.getId());

        // then
        result.andDo(print())
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.code").value("INVALID_REQUEST_BODY"))
              .andExpect(jsonPath("$.message").value("요청 본문이 누락되었거나 형식이 올바르지 않습니다."));

      }
    }

    private ResultActions performUpdateNickname(
        Long userId,
        UserNicknameUpdateRequest request
    ) throws Exception {
      return mockMvc.perform(patch(BASE_URL + "/{id}/nickname", userId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performUpdateNickname(Long userId) throws Exception {
      return mockMvc.perform(patch(BASE_URL + "/{id}/nickname", userId)
          .contentType(MediaType.APPLICATION_JSON));
    }
  }

  @Nested
  @DisplayName("updateProfileImage 메서드는")
  class Describe_updateProfileImage {

    @Nested
    @DisplayName("만약 유효한 프로필 이미지 변경 요청이라면")
    class Context_with_valid_nickname_request {

      @Test
      @DisplayName("프로필 이미지를 변경한다")
      void it_updates_profile_image_with_valid_url() throws Exception {
        // given
        User user = createUser(VALID_EMAIL, VALID_NICKNAME, passwordEncoder.encode(VALID_PASSWORD));
        userRepository.save(user);

        UserProfileImageUpdateRequest request = new UserProfileImageUpdateRequest(
            VALID_PROFILE_IMAGE);

        // when
        ResultActions result = performUpdateProfileImage(user.getId(), request);

        // then
        result.andDo(print())
              .andExpect(status().isNoContent());

        // 실제 DB에서 변경되었는지 확인
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getProfileImageUrl()).isEqualTo(request.url());
      }
    }

    @Nested
    @DisplayName("만약 존재하지 않는 사용자라면")
    class Context_with_non_existing_user_id {

      @Test
      @DisplayName("존재하지 않는 사용자라는 응답을 반환한다")
      void it_returns_user_not_found_error() throws Exception {
        // given
        Long userId = NON_EXISTING_ID;
        UserProfileImageUpdateRequest request = new UserProfileImageUpdateRequest(
            VALID_PROFILE_IMAGE);

        // when
        ResultActions result = performUpdateProfileImage(userId, request);

        // then
        result.andDo(print())
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
              .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."));
      }
    }

//    TODO: 올바르지 않은 URL 형식
//    @Nested
//    @DisplayName("만약 유효하지 않은 프로필 이미지 요청이라면")
//    class Context_with_invalid_profile_image_request {
//
//      @Test
//      @DisplayName("유효하지 않은 URL 형식이면 검증 오류를 반환한다")
//      void it_returns_validation_error_with_invalid_url_format() throws Exception {}

    private ResultActions performUpdateProfileImage(
        Long userId,
        UserProfileImageUpdateRequest request
    ) throws Exception {
      return mockMvc.perform(patch(BASE_URL + "/{id}/profile-image", userId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));
    }
  }

//  TODO: deleteProfileImage()

  @Nested
  @DisplayName("deleteUser 메서드는")
  class Describe_deleteUser {

    @Nested
    @DisplayName("만약 유효한 회원 삭제 요청이라면")
    class Context_with_valid_nickname_request {

      @Test
      @DisplayName("사용자를 삭제한다")
      void it_deletes_users_successfully() throws Exception {
        // given
        User user = createUser(VALID_EMAIL, VALID_NICKNAME, passwordEncoder.encode(VALID_PASSWORD));
        userRepository.save(user);

        assertThat(user.isActive()).isTrue(); // 초기에는 활성 상태

        // when
        ResultActions result = performDeleteUser(user.getId());

        // then
        result.andDo(print())
              .andExpect(status().isNoContent());

        User deletedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(deletedUser.isActive()).isFalse();
      }
    }

    @Nested
    @DisplayName("만약 존재하지 않는 사용자라면")
    class Context_with_non_existing_user_id {

      @Test
      @DisplayName("존재하지 않는 사용자라는 응답을 반환한다")
      void it_returns_user_not_found_error() throws Exception {
        // given
        Long userId = NON_EXISTING_ID;

        // when
        ResultActions result = performDeleteUser(userId);

        // then
        result.andDo(print())
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
              .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."));
      }
    }

    private ResultActions performDeleteUser(Long userId) throws Exception {
      return mockMvc.perform(delete(BASE_URL + "/{id}", userId));
    }
  }

  private User createUser(String email, String nickname, String password) {
    return new User(email, nickname, password);
  }
}
