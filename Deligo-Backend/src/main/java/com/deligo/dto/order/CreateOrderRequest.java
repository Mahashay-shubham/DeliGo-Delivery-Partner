package com.deligo.dto.order;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record CreateOrderRequest(
 @NotBlank @Size(max=1000) String pickupAddress,
 @NotBlank @Size(max=1000) String deliveryAddress,
 @NotBlank @Size(max=1000) String packageDescription,
 @NotNull @DecimalMin(value="0.01") @Digits(integer=6, fraction=2) BigDecimal packageWeight,
 @NotBlank @Size(max=100) String receiverName,
 @NotBlank @Pattern(regexp="^[+0-9() -]{7,20}$") String receiverPhone,
 @Size(max=2000) String deliveryNotes) {}
