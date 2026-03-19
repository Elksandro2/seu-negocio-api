package com.elksandro.seunegocio.models.business.service.exception;

public class BusinessAlreadyExistsException extends RuntimeException {
    public BusinessAlreadyExistsException(String message) {
        super(message);
    }
}