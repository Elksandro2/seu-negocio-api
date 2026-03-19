package com.elksandro.seunegocio.models.order.enums;

public enum OrderStatus {
    PENDING("Pendente"),
    CONFIRMED("Confirmado"),
    PREPARING("Preparando"),
    READY("Pronto/Enviado"),
    COMPLETED("Concluído"),
    CANCELLED("Cancelado");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}