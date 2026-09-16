package com.deligo.dto.order;
import com.deligo.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
public record UpdateStatusRequest(@NotNull OrderStatus status) {}
