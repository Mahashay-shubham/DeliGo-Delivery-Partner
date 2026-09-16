package com.deligo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deligo.dto.auth.UserResponse;
import com.deligo.dto.user.UpdateProfileRequest;
import com.deligo.entity.User;
import com.deligo.service.CurrentUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
  private final CurrentUserService current;

  public UserController(CurrentUserService current) {
    this.current = current;
  }

  @GetMapping("/me")
  public UserResponse me(Authentication authentication) {
    return UserResponse.from(current.get(authentication));
  }

  @PutMapping("/me")
  public UserResponse update(Authentication authentication, @Valid @RequestBody UpdateProfileRequest request) {
    User user = current.get(authentication);
    user.setFullName(request.fullName().trim());
    user.setPhone(request.phone());
    return UserResponse.from(user);
  }
}
