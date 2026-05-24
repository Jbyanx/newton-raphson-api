package com.unimagdalena.newtonraphsonapi.exception;

/**
 * Thrown when the Jacobian matrix is singular or near-singular during
 * Gaussian elimination, making it impossible to compute the correction
 * vector delta-x.
 *
 * This exception indicates that the system of linear equations J·Δx = -F
 * cannot be solved due to an ill-conditioned or singular Jacobian matrix.
 * This can occur when:
 * - The Jacobian determinant is zero or very close to zero
 * - The system has no unique solution at the current approximation
 * - The Newton-Raphson method is diverging or at a critical point
 *
 * @author Newton-Raphson API
 * @version 1.0
 */
public class SingularMatrixException extends RuntimeException {

    /**
     * Constructs a SingularMatrixException with a descriptive message.
     *
     * @param message a descriptive error message explaining the singularity
     */
    public SingularMatrixException(String message) {
        super(message);
    }
}

