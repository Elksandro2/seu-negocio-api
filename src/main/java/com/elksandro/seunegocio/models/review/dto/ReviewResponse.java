package com.elksandro.seunegocio.models.review.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
    Long id,
    String authorName,
    String authorProfilePictureUrl,
    Integer rating,
    String comment,
    LocalDateTime createdAt
) {}