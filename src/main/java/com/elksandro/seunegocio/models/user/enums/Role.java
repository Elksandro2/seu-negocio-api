package com.elksandro.seunegocio.models.user.enums;

public enum Role {
    BUYER("Comprador"),
    SELLER("Vendedor"),
    ADMIN("Administrador");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
