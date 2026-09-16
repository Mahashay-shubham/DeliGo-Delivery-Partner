package com.deligo.dto.user;
import jakarta.validation.constraints.*;
public record UpdateProfileRequest(@NotBlank @Size(max=100) String fullName,
 @Pattern(regexp="^$|^[+0-9() -]{7,20}$", message="Phone number must be valid") String phone) {}
