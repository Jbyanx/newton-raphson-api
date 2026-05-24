package com.unimagdalena.newtonraphsonapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the Newton-Raphson API.
 *
 * Centralizes exception handling across all REST endpoints by catching and
 * transforming exceptions into structured ErrorResponse objects with appropriate
 * HTTP status codes.
 *
 * Handles:
 * - Validation errors (MethodArgumentNotValidException)
 * - Equation parsing errors (EquationParseException)
 * - Singular matrix errors (SingularMatrixException)
 * - Unexpected internal errors (Exception catch-all)
 *
 * @author Newton-Raphson API
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Record representing a standardized error response.
     *
     * @param error a brief error code/category
     * @param message a descriptive error message
     * @param status the HTTP status code
     */
    private record ErrorResponse(String error, String message, int status) {}

    /**
     * Handles validation errors from @Valid annotation on request bodies.
     *
     * Collects all field validation errors and combines them into a single
     * message string, separated by " | ".
     *
     * @param ex the MethodArgumentNotValidException
     * @return ResponseEntity with HTTP 400 and validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .reduce((e1, e2) -> e1 + " | " + e2)
            .orElse("Validation failed");

        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            message,
            HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles errors when parsing or evaluating mathematical expressions.
     *
     * @param ex the EquationParseException
     * @return ResponseEntity with HTTP 422 and parse error details
     */
    @ExceptionHandler(EquationParseException.class)
    public ResponseEntity<ErrorResponse> handleEquationParseException(EquationParseException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            "EQUATION_PARSE_ERROR",
            ex.getMessage(),
            HttpStatus.UNPROCESSABLE_ENTITY.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handles errors when the Jacobian matrix is singular or near-singular.
     *
     * @param ex the SingularMatrixException
     * @return ResponseEntity with HTTP 422 and singular matrix error details
     */
    @ExceptionHandler(SingularMatrixException.class)
    public ResponseEntity<ErrorResponse> handleSingularMatrixException(SingularMatrixException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            "SINGULAR_MATRIX",
            ex.getMessage(),
            HttpStatus.UNPROCESSABLE_ENTITY.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Catch-all exception handler for any unexpected exceptions.
     *
     * @param ex the Exception
     * @return ResponseEntity with HTTP 500 and generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_ERROR",
            "An unexpected error occurred. Please check your input.",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

