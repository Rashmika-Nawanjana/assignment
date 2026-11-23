package com.suntravels.backend.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * Turns validation / business errors into clear JSON for the UI.
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler( MethodArgumentNotValidException.class )
    public ResponseEntity<Map<String, Object>> handleValidation( MethodArgumentNotValidException ex )
    {
        Map<String, String> fieldErrors = new HashMap<>();
        for( FieldError error : ex.getBindingResult().getFieldErrors() )
        {
            fieldErrors.put( error.getField(), error.getDefaultMessage() );
        }

        Map<String, Object> body = new HashMap<>();
        body.put( "message", "Please check the highlighted fields." );
        body.put( "errors", fieldErrors );
        return ResponseEntity.badRequest().body( body );
    }

    @ExceptionHandler( ResponseStatusException.class )
    public ResponseEntity<Map<String, Object>> handleStatus( ResponseStatusException ex )
    {
        Map<String, Object> body = new HashMap<>();
        body.put( "message", ex.getReason() == null ? "Request failed" : ex.getReason() );
        HttpStatus status = HttpStatus.valueOf( ex.getStatusCode().value() );
        return ResponseEntity.status( status ).body( body );
    }
}
