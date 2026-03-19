package com.elksandro.seunegocio.models.business.dto;

import java.util.List;

import com.elksandro.seunegocio.models.item.dto.ItemResponse;
import com.elksandro.seunegocio.models.user.dto.UserSummaryResponse;

public record BusinessResponse(
    Long id,
    String name,
    String description,
    String address,
    String categoryType,
    String categoryDisplayName,
    String logoUrl,
    UserSummaryResponse owner, 
    List<ItemResponse> items
) {}