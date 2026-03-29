package com.elksandro.seunegocio.models.cartItem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.elksandro.seunegocio.models.item.dto.ItemSummaryResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

public record CartItemResponse(
    Long id,
    Integer quantity,   
    BigDecimal subtotal,   
    ItemSummaryResponse item,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime scheduledAt
) {}