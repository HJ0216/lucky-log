package com.fortunehub.luckylog.controller;

import com.fortunehub.luckylog.dto.request.auth.LoginRequest;
import com.fortunehub.luckylog.dto.request.auth.MyNicknameUpdateRequest;
import com.fortunehub.luckylog.dto.request.auth.MyPasswordUpdateRequest;
import com.fortunehub.luckylog.dto.request.auth.MyProfileImageUpdateRequest;
import com.fortunehub.luckylog.dto.request.auth.UserCreateRequest;
import com.fortunehub.luckylog.dto.response.auth.EmailCheckResponse;
import com.fortunehub.luckylog.dto.response.auth.LoginResponse;
import com.fortunehub.luckylog.dto.response.auth.MyProfileResponse;
import com.fortunehub.luckylog.dto.response.user.UserResponse;
import com.fortunehub.luckylog.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth API")
public class AuthController {

  private final AuthService authService;

  @GetMapping("/check-email") // GET /api/v1/auth/check-email
  public ResponseEntity<EmailCheckResponse> checkEmailDuplicate(@RequestParam String email) {
    boolean available = authService.isEmailAvailable(email);
    if (available) {
      return ResponseEntity.status(HttpStatus.OK)
                           .body(new EmailCheckResponse(true, "사용 가능한 이메일입니다."));
    } else {
      return ResponseEntity.status(HttpStatus.CONFLICT)
                           .body(new EmailCheckResponse(false, "이미 사용중인 이메일입니다."));
    }
  }

  @PostMapping("signup") // POST /api/v1/auth/signup
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
    UserResponse response = authService.createUser(request);
    URI location = URI.create("/api/v1/users/" + response.id());
    return ResponseEntity.created(location).body(response);
  }

  @PostMapping("/login") // POST /api/v1/auth/login
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    return ResponseEntity.ok().build();
  }

  @GetMapping("/me")
  public ResponseEntity<MyProfileResponse> getMyProfile(
      @RequestHeader("Authorization") String authorizationHeader) {
    MyProfileResponse response = authService.getMyProfile(authorizationHeader);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/me/nickname")
  public ResponseEntity<MyProfileResponse> updateMyNickname(
      @RequestHeader("Authorization") String authorizationHeader,
      @Valid @RequestBody MyNicknameUpdateRequest request
  ) {
    MyProfileResponse response = authService.updateMyNickname(authorizationHeader, request);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/me/profile-image")
  public ResponseEntity<MyProfileResponse> updateMyProfileImage(
      @RequestHeader("Authorization") String authorizationHeader,
      @Valid @RequestBody MyProfileImageUpdateRequest request
  ) {
    MyProfileResponse response = authService.updateMyProfileImage(authorizationHeader, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/me/profile-image")
  public ResponseEntity<Void> deleteMyProfileImage(
      @RequestHeader("Authorization") String authorizationHeader) {
    authService.deleteMyProfileImage(authorizationHeader);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/me/password")
  public ResponseEntity<Void> changePassword(
      @RequestHeader("Authorization") String authorizationHeader,
      @Valid @RequestBody MyPasswordUpdateRequest request) {
    authService.changePassword(authorizationHeader, request);
    return ResponseEntity.noContent().build();
  }

  // @PostMapping("/forgot-password")

  // @PostMapping("/reset-password")

  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteMyProfile(@RequestHeader("Authorization") String authorizationHeader) {
    authService.deleteMyAccount(authorizationHeader);
    return ResponseEntity.noContent().build();
  }
}
