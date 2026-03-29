package com.elksandro.seunegocio.models.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderResponse(
    Long id,
    String customerName,
    String businessName,
    BigDecimal totalAmount,
    String status,
    String statusDescription,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    List<OrderItemResponse> items
) {}