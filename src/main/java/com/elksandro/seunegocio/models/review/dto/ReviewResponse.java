package com.elksandro.seunegocio.models.review.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ReviewResponse(
    Long id,
    String authorName,
    String authorProfilePictureUrl,
    Integer rating,
    String comment,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt
) {}