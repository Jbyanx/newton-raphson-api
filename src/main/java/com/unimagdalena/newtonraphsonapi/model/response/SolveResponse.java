package com.unimagdalena.newtonraphsonapi.model.response;

import java.util.List;
import java.util.Map;

/**
 * Complete response object returned by the Newton-Raphson solver API.
 *
 * Contains the solution to the 2x2 nonlinear equation system, convergence
 * information, and a detailed history of all iteration steps.
 *
 * @author Newton-Raphson API
 * @version 1.0
 */
public class SolveResponse {

    /**
     * Indicates whether the algorithm converged to a solution.
     * True if max(|Δx|) fell below tolerance before reaching maxIterations.
     * False if the algorithm stopped due to reaching maxIterations without convergence.
     */
    private boolean converged;

    /**
     * Echo of the variable names from the request.
     * Example: ["x", "y"]
     * Used to map solution values to their corresponding variables.
     */
    private List<String> variables;

    /**
     * The computed solution as a map of variable name to final value.
     * Example: {"x": 1.523, "y": 2.477}
     *
     * If converged=true, these are the final values after convergence.
     * If converged=false, these are the last approximation reached before maxIterations.
     */
    private Map<String, Double> solution;

    /**
     * Total number of iterations performed.
     * Equals 0 if the algorithm never entered the main iteration loop,
     * otherwise ranges from 1 to maxIterations.
     */
    private int totalIterations;

    /**
     * The error value from the last iteration: max(|Δx|).
     * Indicates the magnitude of the correction step at the final iteration.
     */
    private double finalError;

    /**
     * List of all iteration steps performed.
     * Provides the complete computational history, useful for debugging and analysis.
     * Each element is an IterationStep detailing the state during that iteration.
     */
    private List<IterationStep> steps;

    // Constructors
    public SolveResponse() {
    }

    public SolveResponse(boolean converged, List<String> variables, Map<String, Double> solution,
                        int totalIterations, double finalError, List<IterationStep> steps) {
        this.converged = converged;
        this.variables = variables;
        this.solution = solution;
        this.totalIterations = totalIterations;
        this.finalError = finalError;
        this.steps = steps;
    }

    // Getters and Setters
    public boolean isConverged() {
        return converged;
    }

    public void setConverged(boolean converged) {
        this.converged = converged;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }

    public Map<String, Double> getSolution() {
        return solution;
    }

    public void setSolution(Map<String, Double> solution) {
        this.solution = solution;
    }

    public int getTotalIterations() {
        return totalIterations;
    }

    public void setTotalIterations(int totalIterations) {
        this.totalIterations = totalIterations;
    }

    public double getFinalError() {
        return finalError;
    }

    public void setFinalError(double finalError) {
        this.finalError = finalError;
    }

    public List<IterationStep> getSteps() {
        return steps;
    }

    public void setSteps(List<IterationStep> steps) {
        this.steps = steps;
    }
}

