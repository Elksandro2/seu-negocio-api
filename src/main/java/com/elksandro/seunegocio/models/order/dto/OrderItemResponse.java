package com.elksandro.seunegocio.models.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    Long id,
    Long itemId,
    String itemName,
    String itemImageUrl,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal
) {}