package com.elksandro.seunegocio.models.cart.dto;

import com.elksandro.seunegocio.models.item.dto.ItemSummaryResponse;

public record CartItemResponse(
    Long id,
    Integer quantity,   
    Double subtotal,   
    ItemSummaryResponse item
) {}