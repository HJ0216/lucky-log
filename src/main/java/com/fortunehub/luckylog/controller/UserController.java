package com.fortunehub.luckylog.controller;

import com.fortunehub.luckylog.dto.request.user.UserNicknameUpdateRequest;
import com.fortunehub.luckylog.dto.request.user.UserProfileImageUpdateRequest;
import com.fortunehub.luckylog.dto.response.user.UserResponse;
import com.fortunehub.luckylog.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController {

  private final UserService userService;

  @GetMapping("/{id}") // GET /api/v1/users/1
  public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
    UserResponse userResponse = userService.getUser(id);
    return ResponseEntity.ok(userResponse);
  }

  @GetMapping("") // GET /api/v1/users
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    List<UserResponse> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @PatchMapping("/{id}/nickname") // PATCH /api/v1/users/1/nickname
  public ResponseEntity<UserResponse> updateNickname(
      @PathVariable Long id,
      @Valid @RequestBody UserNicknameUpdateRequest request) {
    UserResponse response = userService.updateNickname(id, request);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/profile-image") // PATCH /api/v1/users/1/profile-image
  public ResponseEntity<Void> updateProfileImage(
      @PathVariable Long id,
      @Valid @RequestBody UserProfileImageUpdateRequest request
  ) {
    userService.updateProfileImage(id, request);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}/profile-image") // DELETE /api/v1/users/1/profile-image
  public ResponseEntity<Void> deleteProfileImage(@PathVariable Long id) {
    userService.deleteProfileImage(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}") // DELETE /api/v1/users/1
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
  
}
