package com.elksandro.seunegocio.dto.cartItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemRequest(
    @NotNull(message = "O ID do item é obrigatório.")
    Long itemId,
    
    @NotNull(message = "A quantidade é obrigatória.")
    @Min(value = 1, message = "A quantidade deve ser de pelo menos 1.")
    Integer quantity
)
{}