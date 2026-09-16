package com.deligo.service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deligo.dto.order.AssignPartnerRequest;
import com.deligo.dto.order.CreateOrderRequest;
import com.deligo.dto.order.OrderResponse;
import com.deligo.dto.order.StatusHistoryResponse;
import com.deligo.dto.order.UpdateStatusRequest;
import com.deligo.entity.DeliveryOrder;
import com.deligo.entity.OrderStatus;
import com.deligo.entity.OrderStatusHistory;
import com.deligo.entity.User;
import com.deligo.entity.UserRole;
import com.deligo.exception.BadRequestException;
import com.deligo.exception.NotFoundException;
import com.deligo.repository.DeliveryOrderRepository;
import com.deligo.repository.OrderStatusHistoryRepository;
import com.deligo.repository.UserRepository;

@Service
@Transactional
public class OrderService {
  private final DeliveryOrderRepository orders;
  private final OrderStatusHistoryRepository history;
  private final UserRepository users;

  public OrderService(DeliveryOrderRepository orders, OrderStatusHistoryRepository history, UserRepository users) {
    this.orders = orders;
    this.history = history;
    this.users = users;
  }

  public OrderResponse create(User customer, CreateOrderRequest request) {
    DeliveryOrder order = new DeliveryOrder();
    order.setOrderNumber("DLG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    order.setCustomer(customer);
    order.setPickupAddress(request.pickupAddress().trim());
    order.setDeliveryAddress(request.deliveryAddress().trim());
    order.setPackageDescription(request.packageDescription().trim());
    order.setPackageWeight(request.packageWeight());
    order.setReceiverName(request.receiverName().trim());
    order.setReceiverPhone(request.receiverPhone().trim());
    order.setDeliveryNotes(blankToNull(request.deliveryNotes()));
    DeliveryOrder saved = orders.save(order);
    history.save(new OrderStatusHistory(saved, OrderStatus.CREATED, customer));
    return toResponse(saved);
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> listFor(User user, OrderStatus status, String search) {
    List<DeliveryOrder> list = user.getRole() == UserRole.ADMIN
        ? (status == null ? orders.findAll() : orders.findByStatusOrderByCreatedAtDesc(status))
        : user.getRole() == UserRole.DELIVERY_PARTNER ? orders.findByDeliveryPartnerIdOrderByCreatedAtDesc(user.getId())
            : orders.findByCustomerIdOrderByCreatedAtDesc(user.getId());
    return list.stream()
        .filter(
            o -> search == null || search.isBlank() || o.getOrderNumber().toLowerCase().contains(search.toLowerCase())
                || o.getPickupAddress().toLowerCase().contains(search.toLowerCase())
                || o.getDeliveryAddress().toLowerCase().contains(search.toLowerCase()))
        .sorted(Comparator.comparing(DeliveryOrder::getCreatedAt).reversed()).map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public OrderResponse get(User user, UUID id) {
    DeliveryOrder order = find(id);
    ensureAccess(user, order);
    return toResponse(order);
  }

  public OrderResponse updateStatus(User actor, UUID id, UpdateStatusRequest request) {
    DeliveryOrder order = find(id);
    ensureStatusPermission(actor, order, request.status());
    if (!isTransitionAllowed(order.getStatus(), request.status(), actor.getRole()))
      throw new BadRequestException("Invalid status transition from " + order.getStatus() + " to " + request.status());
    order.setStatus(request.status());
    history.save(new OrderStatusHistory(order, request.status(), actor));
    return toResponse(order);
  }

  public OrderResponse assign(User admin, UUID id, AssignPartnerRequest request) {
    DeliveryOrder order = find(id);
    User partner = users.findById(request.deliveryPartnerId())
        .orElseThrow(() -> new NotFoundException("Delivery partner not found"));
    if (partner.getRole() != UserRole.DELIVERY_PARTNER)
      throw new BadRequestException("Selected user is not a delivery partner");
    order.setDeliveryPartner(partner);
    if (order.getStatus() == OrderStatus.CREATED) {
      order.setStatus(OrderStatus.CONFIRMED);
      history.save(new OrderStatusHistory(order, OrderStatus.CONFIRMED, admin));
    }
    return toResponse(order);
  }

  public long count(User customer, Set<OrderStatus> statuses) {
    return orders.countByCustomerIdAndStatusIn(customer.getId(), statuses);
  }

  public long total(User customer) {
    return orders.countByCustomerId(customer.getId());
  }

  public long delivered(User customer) {
    return orders.countByCustomerIdAndStatus(customer.getId(), OrderStatus.DELIVERED);
  }

  private DeliveryOrder find(UUID id) {
    return orders.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
  }

  private void ensureAccess(User user, DeliveryOrder order) {
    if (user.getRole() == UserRole.ADMIN)
      return;
    if (user.getRole() == UserRole.CUSTOMER && order.getCustomer().getId().equals(user.getId()))
      return;
    if (user.getRole() == UserRole.DELIVERY_PARTNER && order.getDeliveryPartner() != null
        && order.getDeliveryPartner().getId().equals(user.getId()))
      return;
    throw new org.springframework.security.access.AccessDeniedException("Denied");
  }

  private void ensureStatusPermission(User user, DeliveryOrder order, OrderStatus status) {
    ensureAccess(user, order);
    if (user.getRole() == UserRole.CUSTOMER && status != OrderStatus.CANCELLED)
      throw new org.springframework.security.access.AccessDeniedException("Denied");
  }

  private boolean isTransitionAllowed(OrderStatus from, OrderStatus to, UserRole role) {
    if (from == to)
      return false;
    if (role == UserRole.ADMIN)
      return from != OrderStatus.DELIVERED && from != OrderStatus.CANCELLED;
    if (to == OrderStatus.CANCELLED)
      return from == OrderStatus.CREATED || from == OrderStatus.CONFIRMED;
    return switch (from) {
      case CREATED -> to == OrderStatus.CONFIRMED;
      case CONFIRMED -> to == OrderStatus.PICKED_UP;
      case PICKED_UP -> to == OrderStatus.IN_TRANSIT;
      case IN_TRANSIT -> to == OrderStatus.DELIVERED;
      default -> false;
    };
  }

  private OrderResponse toResponse(DeliveryOrder order) {
    List<StatusHistoryResponse> track = history.findByOrderIdOrderByChangedAtAsc(order.getId()).stream()
        .map(StatusHistoryResponse::from).toList();
    User p = order.getDeliveryPartner();
    return new OrderResponse(order.getId(), order.getOrderNumber(), order.getPickupAddress(),
        order.getDeliveryAddress(), order.getPackageDescription(), order.getPackageWeight(), order.getReceiverName(),
        order.getReceiverPhone(), order.getDeliveryNotes(), order.getStatus(), order.getCreatedAt(),
        order.getUpdatedAt(), order.getCustomer().getId(), order.getCustomer().getFullName(),
        p == null ? null : p.getId(), p == null ? null : p.getFullName(), track);
  }

  private String blankToNull(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }
}
