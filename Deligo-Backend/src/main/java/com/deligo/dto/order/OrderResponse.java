package com.deligo.dto.order;
import com.deligo.entity.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
public record OrderResponse(UUID id, String orderNumber, String pickupAddress, String deliveryAddress,
 String packageDescription, BigDecimal packageWeight, String receiverName, String receiverPhone,
 String deliveryNotes, OrderStatus status, Instant createdAt, Instant updatedAt,
 UUID customerId, String customerName, UUID deliveryPartnerId, String deliveryPartnerName, List<StatusHistoryResponse> tracking) {}
