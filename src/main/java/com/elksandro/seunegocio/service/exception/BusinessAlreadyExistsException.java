package com.elksandro.seunegocio.service.exception;

public class BusinessAlreadyExistsException extends RuntimeException {
    public BusinessAlreadyExistsException(String message) {
        super(message);
    }
}