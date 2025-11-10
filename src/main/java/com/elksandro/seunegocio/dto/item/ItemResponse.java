package com.elksandro.seunegocio.dto.item;

import com.elksandro.seunegocio.dto.business.BusinessSummaryResponse;
import com.elksandro.seunegocio.model.enums.OfferType;

public record ItemResponse(
    Long id,
    String name,
    String description,
    Double price,
    OfferType offerType,
    String imageUrl,
    BusinessSummaryResponse business
) {}