package com.deligo.repository;

import com.deligo.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtAsc(UUID orderId);
}
