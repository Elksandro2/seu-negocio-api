package com.elksandro.seunegocio.models.order.dto;

import java.math.BigDecimal;

import com.elksandro.seunegocio.models.item.enums.OfferType;

public record OrderItemResponse(
    Long id,
    Long itemId,
    String itemName,
    String itemImageUrl,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal,
    OfferType offerType
) {}