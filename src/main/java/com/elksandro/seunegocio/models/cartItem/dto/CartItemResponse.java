package com.elksandro.seunegocio.models.cartItem.dto;

import java.math.BigDecimal;

import com.elksandro.seunegocio.models.item.dto.ItemSummaryResponse;

public record CartItemResponse(
    Long id,
    Integer quantity,   
    BigDecimal subtotal,   
    ItemSummaryResponse item
) {}