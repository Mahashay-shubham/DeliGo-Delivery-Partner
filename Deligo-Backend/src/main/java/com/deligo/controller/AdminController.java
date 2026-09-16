package com.deligo.controller;
import com.deligo.dto.admin.*;
import com.deligo.dto.auth.UserResponse;
import com.deligo.dto.order.*;
import com.deligo.entity.*;
import com.deligo.exception.*;
import com.deligo.repository.*;
import com.deligo.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/admin") @PreAuthorize("hasRole('ADMIN')")
public class AdminController {
 private final UserRepository users; private final DeliveryOrderRepository orders; private final OrderService orderService; private final CurrentUserService current;
 public AdminController(UserRepository users,DeliveryOrderRepository orders,OrderService orderService,CurrentUserService current){this.users=users;this.orders=orders;this.orderService=orderService;this.current=current;}
 @GetMapping("/dashboard") public DashboardResponse dashboard(){return new DashboardResponse(users.count(),orders.count(),orders.countByStatus(OrderStatus.CREATED),orders.countByStatus(OrderStatus.IN_TRANSIT),orders.countByStatus(OrderStatus.DELIVERED));}
 @GetMapping("/users") public List<UserResponse> users(@RequestParam(required=false) String search,@RequestParam(required=false) UserRole role){ List<User> list=role==null?users.findAll():users.findByRoleOrderByCreatedAtDesc(role); return list.stream().filter(u->search==null||search.isBlank()||u.getFullName().toLowerCase().contains(search.toLowerCase())||u.getEmail().toLowerCase().contains(search.toLowerCase())).map(UserResponse::from).toList(); }
 @PatchMapping("/users/{id}/role") public UserResponse role(@PathVariable UUID id,@Valid @RequestBody UpdateRoleRequest request){User user=users.findById(id).orElseThrow(()->new NotFoundException("User not found")); user.setRole(request.role()); return UserResponse.from(user);}
 @PatchMapping("/orders/{id}/assign") public OrderResponse assign(Authentication authentication,@PathVariable UUID id,@Valid @RequestBody AssignPartnerRequest request){return orderService.assign(current.get(authentication),id,request);}
}
