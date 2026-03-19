package com.elksandro.seunegocio.models.item.dto;

import com.elksandro.seunegocio.models.item.enums.OfferType;

public record ItemSummaryResponse(
    Long id,
    String name,
    Double price,
    Long stockQuantity,
    String imageUrl,
    OfferType offerType,
    String businessName
) {}