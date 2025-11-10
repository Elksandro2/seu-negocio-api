package com.elksandro.seunegocio.dto.business;

import java.util.List;

import com.elksandro.seunegocio.dto.item.ItemResponse;
import com.elksandro.seunegocio.dto.user.UserSummaryResponse;

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