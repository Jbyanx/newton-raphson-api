package com.unimagdalena.newtonraphsonapi.model.request;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * Data Transfer Object for Newton-Raphson equation solver requests.
 *
 * Represents a system of 2 nonlinear equations in 2 variables to be solved
 * using the Newton-Raphson method.
 *
 * @author Newton-Raphson API
 * @version 1.0
 */
public class SolveRequest {

    /**
     * List of exactly 2 mathematical expressions representing the equations.
     * Each expression is a function of variables, equal to zero.
     *
     * Example: ["x^2 + y - 4", "x + y^2 - 6"]
     * These represent f1(x,y) = x^2 + y - 4 = 0 and f2(x,y) = x + y^2 - 6 = 0
     */
    @NotNull(message = "Equations list cannot be null")
    @Size(min = 2, max = 2, message = "Exactly 2 equations are required")
    private List<@NotBlank(message = "Each equation cannot be blank") String> equations;

    /**
     * List of exactly 2 variable names used in the equations.
     *
     * Example: ["x", "y"]
     */
    @NotNull(message = "Variables list cannot be null")
    @Size(min = 2, max = 2, message = "Exactly 2 variables are required")
    private List<@NotBlank(message = "Each variable name cannot be blank") String> variables;

    /**
     * Initial approximation vector with exactly 2 values.
     * Used as the starting point x0 for the Newton-Raphson iteration.
     *
     * Example: [1.0, 1.0]
     */
    @NotNull(message = "Initial guess cannot be null")
    @Size(min = 2, max = 2, message = "Initial guess must have exactly 2 values")
    private List<Double> initialGuess;

    /**
     * Maximum number of iterations allowed before stopping.
     * Optional; defaults to 100.
     * Must be between 1 and 500.
     */
    @Min(value = 1, message = "Max iterations must be at least 1")
    @Max(value = 500, message = "Max iterations cannot exceed 500")
    private Integer maxIterations = 100;

    /**
     * Convergence tolerance for the error criterion.
     * The algorithm stops when max(|Δx|) < tolerance.
     * Optional; defaults to 1e-7.
     * Must be a positive value.
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "Tolerance must be positive")
    private Double tolerance = 1e-7;

    // Constructors
    public SolveRequest() {
    }

    public SolveRequest(List<String> equations, List<String> variables, List<Double> initialGuess) {
        this.equations = equations;
        this.variables = variables;
        this.initialGuess = initialGuess;
    }

    // Getters and Setters
    public List<String> getEquations() {
        return equations;
    }

    public void setEquations(List<String> equations) {
        this.equations = equations;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    public List<Double> getInitialGuess() {
        return initialGuess;
    }

    public void setInitialGuess(List<Double> initialGuess) {
        this.initialGuess = initialGuess;
    }

    public Integer getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(Integer maxIterations) {
        this.maxIterations = maxIterations;
    }

    public Double getTolerance() {
        return tolerance;
    }

    public void setTolerance(Double tolerance) {
        this.tolerance = tolerance;
    }
}

