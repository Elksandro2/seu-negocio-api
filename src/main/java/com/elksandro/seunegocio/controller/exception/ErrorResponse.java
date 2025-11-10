package com.elksandro.seunegocio.controller.exception;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private Instant timestamp;
    private Integer code;
    private String message;
    private String path;
}
