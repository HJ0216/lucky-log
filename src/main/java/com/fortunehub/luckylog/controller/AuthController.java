package com.fortunehub.luckylog.controller;

import com.fortunehub.luckylog.dto.request.LoginRequest;
import com.fortunehub.luckylog.dto.request.UserCreateRequest;
import com.fortunehub.luckylog.dto.response.EmailCheckResponse;
import com.fortunehub.luckylog.dto.response.LoginResponse;
import com.fortunehub.luckylog.dto.response.UserResponse;
import com.fortunehub.luckylog.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
  public ResponseEntity<UserResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
    UserResponse response = authService.getCurrentUser(token);
    return ResponseEntity.ok(response);
  }
}
