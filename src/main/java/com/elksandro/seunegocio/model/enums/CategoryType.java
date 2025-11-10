package com.elksandro.seunegocio.model.enums;

public enum CategoryType {
    FOOD_DRINKS("Alimentos e Bebidas"),
    BEAUTY_CARE("Beleza e Cuidados Pessoais"),
    HEALTH("Saúde e Bem-estar"),
    CONSTRUCTION("Construção e Reformas"),
    TRANSPORT("Transporte e Logística"),
    RENTALS("Aluguéis e Imóveis"),
    SERVICES_GENERAL("Serviços Gerais e Aulas"),
    OTHERS("Outros");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
