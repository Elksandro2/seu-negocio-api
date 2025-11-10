package com.elksandro.seunegocio.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdate(
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    String name,

    @Size(max = 20, message = "O WhatsApp deve ter no máximo 20 caracteres.")
    String whatsapp
) {}