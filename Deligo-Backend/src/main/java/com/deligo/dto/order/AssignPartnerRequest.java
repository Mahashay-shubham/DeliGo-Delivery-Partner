package com.deligo.dto.order;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record AssignPartnerRequest(@NotNull UUID deliveryPartnerId) {}
