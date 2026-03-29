package com.elksandro.seunegocio.models.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.elksandro.seunegocio.models.item.enums.OfferType;
import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderItemResponse(
    Long id,
    Long itemId,
    String itemName,
    String itemImageUrl,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal,
    OfferType offerType,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime scheduledAt
) {}