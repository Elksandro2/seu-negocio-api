package com.elksandro.seunegocio.dto.cartItem;

import com.elksandro.seunegocio.dto.item.ItemSummaryResponse;

public record CartItemResponse(
    Long id,
    Integer quantity,   
    Double subtotal,   
    ItemSummaryResponse item
) {}