package com.deligo.controller;
import com.deligo.dto.dashboard.CustomerDashboardResponse;
import com.deligo.entity.*;
import com.deligo.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/dashboard")
public class DashboardController {
 private final CurrentUserService current; private final OrderService orders;
 public DashboardController(CurrentUserService current,OrderService orders){this.current=current;this.orders=orders;}
 @GetMapping public CustomerDashboardResponse customer(Authentication authentication){User user=current.get(authentication); if(user.getRole()!=UserRole.CUSTOMER) return new CustomerDashboardResponse(0,0,0,List.of()); List<com.deligo.dto.order.OrderResponse> recent=orders.listFor(user,null,null).stream().limit(5).toList(); return new CustomerDashboardResponse(orders.total(user),orders.count(user,Set.of(OrderStatus.CREATED,OrderStatus.CONFIRMED,OrderStatus.PICKED_UP,OrderStatus.IN_TRANSIT)),orders.delivered(user),recent);}
}
