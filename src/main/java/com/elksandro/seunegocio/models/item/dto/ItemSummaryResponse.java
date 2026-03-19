package com.elksandro.seunegocio.models.item.dto;

import java.math.BigDecimal;

import com.elksandro.seunegocio.models.item.enums.OfferType;

public record ItemSummaryResponse(
    Long id,
    String name,
    BigDecimal price,
    Integer stockQuantity,
    String imageUrl,
    OfferType offerType,
    String businessName
) {}