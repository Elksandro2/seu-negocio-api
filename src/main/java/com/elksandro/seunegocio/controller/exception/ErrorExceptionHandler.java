package com.elksandro.seunegocio.controller.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.elksandro.seunegocio.service.exception.BusinessNotFoundException;
import com.elksandro.seunegocio.service.exception.ItemNotFoundException;
import com.elksandro.seunegocio.service.exception.UnauthorizedException;
import com.elksandro.seunegocio.service.exception.UserAlreadyExistsException;
import com.elksandro.seunegocio.service.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ErrorExceptionHandler {
    
    private ResponseEntity<ErrorResponse> createErrorResponseEntity(HttpStatus status, String message, String path) {
        ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            status.value(),
            message,
            path
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> validationErro(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        ValidationError validationErro = new ValidationError(
            Instant.now(), 
            status.value(), 
            "Erro de validação: Um ou mais campos são inválidos.", 
            request.getRequestURI()
        );
        
        for (FieldError error : e.getFieldErrors()) {
            validationErro.addErro(error.getField(), error.getDefaultMessage());
        }
        
        return ResponseEntity.status(status).body(validationErro);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> userAlreadyExistsErro(UserAlreadyExistsException e, HttpServletRequest request) {
        return createErrorResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgumentErro(IllegalArgumentException e, HttpServletRequest request) {
        return createErrorResponseEntity(HttpStatus.BAD_REQUEST, "Argumento inválido: " + e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({UserNotFoundException.class, BusinessNotFoundException.class, ItemNotFoundException.class})
    public ResponseEntity<ErrorResponse> resourceNotFoundErro(RuntimeException e, HttpServletRequest request) {
        return createErrorResponseEntity(HttpStatus.NOT_FOUND, e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> unauthorizedErro(UnauthorizedException e, HttpServletRequest request) {
        return createErrorResponseEntity(HttpStatus.FORBIDDEN, e.getMessage(), request.getRequestURI());
    }
}
