package com.elksandro.seunegocio.dto.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ItemStockUpdate(
    @NotNull(message = "A quantidade em estoque é obrigatória.")
    @PositiveOrZero(message = "A quantidade deve ser zero ou positiva.")
    Long stockQuantity
) {}