package com.deligo.dto.admin;
import com.deligo.entity.UserRole;
import jakarta.validation.constraints.NotNull;
public record UpdateRoleRequest(@NotNull UserRole role) {}
