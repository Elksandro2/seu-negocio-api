package com.elksandro.seunegocio.dto.item;

import com.elksandro.seunegocio.model.enums.CategoryType;
import com.elksandro.seunegocio.model.enums.OfferType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ItemRequest(
    @NotBlank(message = "O nome do item é obrigatório.")
    @Size(max = 100, message = "O nome não pode exceder 100 caracteres.")
    String name,
    
    @NotBlank(message = "A descrição é obrigatória.")
    String description,

    @PositiveOrZero(message = "O preço deve ser zero ou positivo.")
    Double price,

    @NotNull(message = "O tipo de oferta é obrigatório (PRODUCT ou SERVICE).")
    OfferType offerType,

    @NotNull(message = "A categoria é obrigatória.")
    CategoryType categoryType,

    @NotNull(message = "O ID do vendedor é obrigatório.")
    Long userId,
    
    @NotNull(message = "O ID do Negócio é obrigatório.")
    Long businessId
) {}