package com.elksandro.seunegocio.service.exception;

public class BusinessNotFoundException extends RuntimeException {
    public BusinessNotFoundException(String message) {
        super(message);
    }
}