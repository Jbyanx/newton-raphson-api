package com.unimagdalena.newtonraphsonapi.exception;

/**
 * Thrown when one or more equations cannot be parsed or evaluated by exp4j.
 * Usually indicates a malformed mathematical expression.
 *
 * This exception is typically raised when:
 * - An equation contains undefined variables not in the solver's variable list
 * - An equation uses invalid or unsupported mathematical syntax
 * - An equation cannot be compiled by the exp4j parser
 *
 * @author Newton-Raphson API
 * @version 1.0
 */
public class EquationParseException extends RuntimeException {

    /**
     * Constructs an EquationParseException with a message and cause.
     *
     * @param message a descriptive error message
     * @param cause the underlying exception that caused this parse error
     */
    public EquationParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

