package com.elksandro.seunegocio.models.item.dto;

import com.elksandro.seunegocio.models.business.dto.BusinessSummaryResponse;
import com.elksandro.seunegocio.models.item.enums.OfferType;

public record ItemResponse(
    Long id,
    String name,
    String description,
    Double price,
    Long stockQuantity,
    OfferType offerType,
    String imageUrl,
    BusinessSummaryResponse business
) {}