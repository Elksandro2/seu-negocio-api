package com.elksandro.seunegocio.models.user.dto;

public record UserSummaryResponse(
    Long id,
    String name,
    String whatsapp,
    String profilePictureUrl,
    String role
) {}