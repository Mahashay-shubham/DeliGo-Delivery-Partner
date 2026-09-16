package com.deligo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.deligo.dto.order.CreateOrderRequest;
import com.deligo.dto.order.OrderResponse;
import com.deligo.dto.order.UpdateStatusRequest;
import com.deligo.entity.OrderStatus;
import com.deligo.service.CurrentUserService;
import com.deligo.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {
  private final OrderService orders;
  private final CurrentUserService current;

  public OrderController(OrderService orders, CurrentUserService current) {
    this.orders = orders;
    this.current = current;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OrderResponse create(Authentication authentication, @Valid @RequestBody CreateOrderRequest request) {
    return orders.create(current.get(authentication), request);
  }

  @GetMapping
  public List<OrderResponse> list(Authentication authentication, @RequestParam(required = false) OrderStatus status,
      @RequestParam(required = false) String search) {
    return orders.listFor(current.get(authentication), status, search);
  }

  @GetMapping("/{id}")
  public OrderResponse get(Authentication authentication, @PathVariable UUID id) {
    return orders.get(current.get(authentication), id);
  }

  @PatchMapping("/{id}/status")
  public OrderResponse status(Authentication authentication, @PathVariable UUID id,
      @Valid @RequestBody UpdateStatusRequest request) {
    return orders.updateStatus(current.get(authentication), id, request);
  }
}
