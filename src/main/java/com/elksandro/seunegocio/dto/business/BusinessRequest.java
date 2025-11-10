package com.elksandro.seunegocio.dto.business;

import com.elksandro.seunegocio.model.enums.CategoryType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BusinessRequest(
    @NotBlank(message = "O nome do negócio é obrigatório.")
    @Size(max = 100, message = "O nome não pode exceder 100 caracteres.")
    String name,

    @NotBlank(message = "A descrição é obrigatória.")
    String description,

    String address,

    @NotNull(message = "A categoria é obrigatória.")
    CategoryType categoryType
) {}