package com.fortunehub.luckylog.controller;

import com.fortunehub.luckylog.dto.request.UserCreateRequest;
import com.fortunehub.luckylog.dto.response.UserResponse;
import com.fortunehub.luckylog.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @PostMapping("/user") // POST /api/v1/user
  public ResponseEntity<Void> createUser(@Valid @RequestBody UserCreateRequest request){
    long userId = userService.createUser(request);
    URI location = URI.create("/api/v1/user/" + userId);
    return ResponseEntity.created(location).build();
  }
}
