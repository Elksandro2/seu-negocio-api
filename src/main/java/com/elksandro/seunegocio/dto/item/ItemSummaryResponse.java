package com.elksandro.seunegocio.dto.item;

import com.elksandro.seunegocio.model.enums.OfferType;

public record ItemSummaryResponse(
    Long id,
    String name,
    Double price,
    String imageUrl,
    OfferType offerType,
    String businessName
) {}