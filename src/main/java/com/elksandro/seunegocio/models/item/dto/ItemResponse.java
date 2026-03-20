package com.elksandro.seunegocio.models.item.dto;

import java.math.BigDecimal;
import java.util.List;

import com.elksandro.seunegocio.models.business.dto.BusinessSummaryResponse;
import com.elksandro.seunegocio.models.item.enums.OfferType;

public record ItemResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stockQuantity,
    OfferType offerType,
    List<String> imageUrls,
    BusinessSummaryResponse business
) {}