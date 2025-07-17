package com.fortunehub.luckylog.controller;

import com.fortunehub.luckylog.dto.request.UserCreateRequest;
import com.fortunehub.luckylog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

  @PostMapping("/user") // POST /api/v1/user
  public void createUser(@Valid @RequestBody UserCreateRequest request){
    userService.createUser(request);
  }
}
