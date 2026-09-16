package com.deligo.dto.dashboard;
import com.deligo.dto.order.OrderResponse;
import java.util.List;
public record CustomerDashboardResponse(long totalOrders,long pendingOrders,long deliveredOrders,List<OrderResponse> recentOrders) {}
