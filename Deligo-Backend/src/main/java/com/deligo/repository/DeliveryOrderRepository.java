package com.deligo.repository;

import com.deligo.entity.DeliveryOrder;
import com.deligo.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, UUID> {
    List<DeliveryOrder> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    List<DeliveryOrder> findByDeliveryPartnerIdOrderByCreatedAtDesc(UUID partnerId);
    List<DeliveryOrder> findByStatusOrderByCreatedAtDesc(OrderStatus status);
    Optional<DeliveryOrder> findByIdAndCustomerId(UUID id, UUID customerId);
    long countByCustomerId(UUID customerId);
    long countByCustomerIdAndStatusIn(UUID customerId, Collection<OrderStatus> statuses);
    long countByCustomerIdAndStatus(UUID customerId, OrderStatus status);
    long countByStatus(OrderStatus status);
}
