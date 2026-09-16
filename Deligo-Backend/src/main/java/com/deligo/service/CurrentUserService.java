package com.deligo.service;
import com.deligo.entity.User;
import com.deligo.exception.NotFoundException;
import com.deligo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
@Service
public class CurrentUserService {
 private final UserRepository users;
 public CurrentUserService(UserRepository users) { this.users = users; }
 public User get(Authentication authentication) { return users.findByEmail(authentication.getName()).orElseThrow(() -> new NotFoundException("User not found")); }
}
