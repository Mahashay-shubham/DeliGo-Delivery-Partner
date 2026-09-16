package com.deligo.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "order_id", nullable = false)
    private DeliveryOrder order;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private OrderStatus status;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "changed_by_id")
    private User changedBy;
    @Column(nullable = false, updatable = false)
    private Instant changedAt;

    protected OrderStatusHistory() {}
    public OrderStatusHistory(DeliveryOrder order, OrderStatus status, User changedBy) {
        this.order = order; this.status = status; this.changedBy = changedBy; this.changedAt = Instant.now();
    }
    public OrderStatus getStatus() { return status; }
    public Instant getChangedAt() { return changedAt; }
    public User getChangedBy() { return changedBy; }
}
