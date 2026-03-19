package com.elksandro.seunegocio.models.item.dto;

import java.math.BigDecimal;

import com.elksandro.seunegocio.models.item.enums.OfferType;

public record ItemSummaryResponse(
    Long id,
    String name,
    BigDecimal price,
    Long stockQuantity,
    String imageUrl,
    OfferType offerType,
    String businessName
) {}