package com.fortunehub.luckylog.controller;

import com.fortunehub.luckylog.dto.request.UserCreateRequest;
import com.fortunehub.luckylog.dto.request.UserNicknameUpdateRequest;
import com.fortunehub.luckylog.dto.response.EmailCheckResponse;
import com.fortunehub.luckylog.dto.response.UserResponse;
import com.fortunehub.luckylog.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController {

  private final UserService userService;

  @GetMapping("/user/{id}") // GET /api/user/1
  public ResponseEntity<UserResponse> getUser(@PathVariable Long id){
    UserResponse userResponse = userService.getUser(id);
    return ResponseEntity.ok(userResponse);
  }

  @GetMapping("/user/check-email") // GET /api/v1/user/check-email?email=:email
  public ResponseEntity<EmailCheckResponse> checkEmailDuplicate(@RequestParam String email){
    boolean available = userService.isEmailAvailable(email);
    if (available) {
      return ResponseEntity.ok(
          new EmailCheckResponse(true, "사용 가능한 이메일입니다.")
      );
    } else {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(
          new EmailCheckResponse(false, "이미 사용중인 이메일입니다.")
      );
    }
  }

  @PostMapping("/user") // POST /api/v1/user
  public ResponseEntity<Void> createUser(@Valid @RequestBody UserCreateRequest request){
    long userId = userService.createUser(request);
    URI location = URI.create("/api/v1/user/" + userId);
    return ResponseEntity.created(location).build();
  }

  @PatchMapping("/user/{id}/nickname") // PATCH /api/v1/user/1/nickname
  public ResponseEntity<UserResponse> updateNickname(
      @PathVariable Long id,
      @RequestBody UserNicknameUpdateRequest request){
    UserResponse response = userService.updateNickname(id, request);
    return ResponseEntity.ok(response);
  }
}
