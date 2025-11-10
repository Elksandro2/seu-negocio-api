package com.elksandro.seunegocio.dto.user;

public record UserSummaryResponse(
    Long id,
    String name,
    String whatsapp
) {}