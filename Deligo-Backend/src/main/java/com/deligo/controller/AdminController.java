package com.deligo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deligo.dto.admin.DashboardResponse;
import com.deligo.dto.admin.UpdateRoleRequest;
import com.deligo.dto.auth.UserResponse;
import com.deligo.dto.order.AssignPartnerRequest;
import com.deligo.dto.order.OrderResponse;
import com.deligo.entity.OrderStatus;
import com.deligo.entity.User;
import com.deligo.entity.UserRole;
import com.deligo.exception.NotFoundException;
import com.deligo.repository.DeliveryOrderRepository;
import com.deligo.repository.UserRepository;
import com.deligo.service.CurrentUserService;
import com.deligo.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
  private final UserRepository users;
  private final DeliveryOrderRepository orders;
  private final OrderService orderService;
  private final CurrentUserService current;

  public AdminController(UserRepository users, DeliveryOrderRepository orders, OrderService orderService,
      CurrentUserService current) {
    this.users = users;
    this.orders = orders;
    this.orderService = orderService;
    this.current = current;
  }

  @GetMapping("/dashboard")
  public DashboardResponse dashboard() {
    return new DashboardResponse(users.count(), orders.count(), orders.countByStatus(OrderStatus.CREATED),
        orders.countByStatus(OrderStatus.IN_TRANSIT), orders.countByStatus(OrderStatus.DELIVERED));
  }

  @GetMapping("/users")
  public List<UserResponse> users(@RequestParam(required = false) String search,
      @RequestParam(required = false) UserRole role) {
    List<User> list = role == null ? users.findAll() : users.findByRoleOrderByCreatedAtDesc(role);
    return list.stream()
        .filter(u -> search == null || search.isBlank() || u.getFullName().toLowerCase().contains(search.toLowerCase())
            || u.getEmail().toLowerCase().contains(search.toLowerCase()))
        .map(UserResponse::from).toList();
  }

  @PatchMapping("/users/{id}/role")
  public UserResponse role(@PathVariable UUID id, @Valid @RequestBody UpdateRoleRequest request) {
    User user = users.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    user.setRole(request.role());
    users.save(user);
    return UserResponse.from(user);
  }

  @PatchMapping("/orders/{id}/assign")
  public OrderResponse assign(Authentication authentication, @PathVariable UUID id,
      @Valid @RequestBody AssignPartnerRequest request) {
    return orderService.assign(current.get(authentication), id, request);
  }
}
