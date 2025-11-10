package com.elksandro.seunegocio.controller.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ValidationError extends ErrorResponse {
    private final List<FieldMessage> fieldMessages = new ArrayList<>();

    public ValidationError(Instant timestamp, Integer code, String message, String path) {
        super(timestamp, code, message, path);   
    }

    public void addErro(String field, String message) {
        this.fieldMessages.add(new FieldMessage(field, message));
    }
}
