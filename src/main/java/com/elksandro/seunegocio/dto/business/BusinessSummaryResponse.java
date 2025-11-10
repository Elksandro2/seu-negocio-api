package com.elksandro.seunegocio.dto.business;

public record BusinessSummaryResponse(
    Long id,
    String name,
    String address,
    String categoryType
) {}