package com.deligo.dto.admin;
public record DashboardResponse(long totalUsers, long totalOrders, long createdOrders, long inTransitOrders, long deliveredOrders) {}
