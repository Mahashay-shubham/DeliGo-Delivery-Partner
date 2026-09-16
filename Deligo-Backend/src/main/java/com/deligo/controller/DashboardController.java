package com.deligo.controller;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deligo.dto.dashboard.CustomerDashboardResponse;
import com.deligo.entity.OrderStatus;
import com.deligo.entity.User;
import com.deligo.entity.UserRole;
import com.deligo.service.CurrentUserService;
import com.deligo.service.OrderService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
  private final CurrentUserService current;
  private final OrderService orders;

  public DashboardController(CurrentUserService current, OrderService orders) {
    this.current = current;
    this.orders = orders;
  }

  @GetMapping
  public CustomerDashboardResponse customer(Authentication authentication) {
    User user = current.get(authentication);
    if (user.getRole() != UserRole.CUSTOMER)
      return new CustomerDashboardResponse(0, 0, 0, List.of());
    List<com.deligo.dto.order.OrderResponse> recent = orders.listFor(user, null, null).stream().limit(5).toList();
    return new CustomerDashboardResponse(orders.total(user),
        orders.count(user,
            Set.of(OrderStatus.CREATED, OrderStatus.CONFIRMED, OrderStatus.PICKED_UP, OrderStatus.IN_TRANSIT)),
        orders.delivered(user), recent);
  }
}
