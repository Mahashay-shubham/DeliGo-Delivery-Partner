package com.deligo.dto.order;
import com.deligo.entity.*;
import java.time.Instant;
public record StatusHistoryResponse(OrderStatus status, Instant changedAt, String changedBy) {
 public static StatusHistoryResponse from(OrderStatusHistory history) { return new StatusHistoryResponse(history.getStatus(), history.getChangedAt(), history.getChangedBy() == null ? null : history.getChangedBy().getFullName()); }
}
