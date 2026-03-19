package com.elksandro.seunegocio.models.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    String customerName,
    String businessName,
    BigDecimal totalAmount,
    String status,
    String statusDescription,
    LocalDateTime createdAt,
    List<OrderItemResponse> items
) {}