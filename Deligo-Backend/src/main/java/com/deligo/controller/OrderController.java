package com.deligo.controller;
import com.deligo.dto.order.*;
import com.deligo.entity.OrderStatus;
import com.deligo.service.CurrentUserService;
import com.deligo.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/orders")
public class OrderController {
 private final OrderService orders; private final CurrentUserService current;
 public OrderController(OrderService orders, CurrentUserService current) { this.orders=orders; this.current=current; }
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public OrderResponse create(Authentication authentication,@Valid @RequestBody CreateOrderRequest request) { return orders.create(current.get(authentication),request); }
 @GetMapping public List<OrderResponse> list(Authentication authentication,@RequestParam(required=false) OrderStatus status,@RequestParam(required=false) String search) { return orders.listFor(current.get(authentication),status,search); }
 @GetMapping("/{id}") public OrderResponse get(Authentication authentication,@PathVariable UUID id) { return orders.get(current.get(authentication),id); }
 @PatchMapping("/{id}/status") public OrderResponse status(Authentication authentication,@PathVariable UUID id,@Valid @RequestBody UpdateStatusRequest request) { return orders.updateStatus(current.get(authentication),id,request); }
}
