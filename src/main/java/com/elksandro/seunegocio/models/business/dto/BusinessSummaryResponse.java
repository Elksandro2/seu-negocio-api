package com.elksandro.seunegocio.models.business.dto;

public record BusinessSummaryResponse(
    Long id,
    String name,
    String address,
    String categoryType,
    String logoUrl,
    String ownerWhatsapp
) {}