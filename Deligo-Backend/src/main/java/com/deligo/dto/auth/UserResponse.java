package com.deligo.dto.auth;

import com.deligo.entity.User;
import com.deligo.entity.UserRole;

import java.util.UUID;

public record UserResponse(UUID id, String fullName, String email, String phone, UserRole role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getRole());
    }
}
