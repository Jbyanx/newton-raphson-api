package com.unimagdalena.newtonraphsonapi.model.response;

/**
 * Represents a single iteration step of the Newton-Raphson method.
 *
 * Contains all the computed values during one step of the iterative process,
 * including the current approximation, function values, Jacobian matrix,
 * correction vector, and error measurement.
 *
 * @author Newton-Raphson API
 * @version 1.0
 */
public class IterationStep {

    /**
     * The iteration number (starting from 1).
     * Identifies which iteration this step represents in the overall process.
     */
    private int iterationNumber;

    /**
     * Current approximation vector at this iteration: [x, y]
     * Represents the current estimate of the solution.
     */
    private double[] xValues;

    /**
     * Function values F(x) evaluated at xValues.
     * Computed as [f1(x,y), f2(x,y)] for the current approximation.
     * Used to determine convergence and compute the correction.
     */
    private double[] fValues;

    /**
     * The 2x2 Jacobian matrix computed numerically at xValues.
     * J = [[∂f1/∂x, ∂f1/∂y], [∂f2/∂x, ∂f2/∂y]]
     * Partial derivatives are approximated using finite differences.
     */
    private double[][] jacobian;

    /**
     * The correction vector Δx solved from the linear system J·Δx = -F.
     * Represents the step to update the current approximation.
     */
    private double[] deltaX;

    /**
     * The next approximation vector: xValues + deltaX.
     * This becomes the current approximation in the next iteration.
     */
    private double[] nextXValues;

    /**
     * The convergence error: max(|Δx|) of this iteration.
     * Used as the criterion to determine convergence.
     * When this value falls below the specified tolerance, the algorithm stops.
     */
    private double error;

    // Constructors
    public IterationStep() {
    }

    public IterationStep(int iterationNumber, double[] xValues, double[] fValues,
                        double[][] jacobian, double[] deltaX, double[] nextXValues, double error) {
        this.iterationNumber = iterationNumber;
        this.xValues = xValues;
        this.fValues = fValues;
        this.jacobian = jacobian;
        this.deltaX = deltaX;
        this.nextXValues = nextXValues;
        this.error = error;
    }

    // Getters and Setters
    public int getIterationNumber() {
        return iterationNumber;
    }

    public void setIterationNumber(int iterationNumber) {
        this.iterationNumber = iterationNumber;
    }

    public double[] getXValues() {
        return xValues;
    }

    public void setXValues(double[] xValues) {
        this.xValues = xValues;
    }

    public double[] getFValues() {
        return fValues;
    }

    public void setFValues(double[] fValues) {
        this.fValues = fValues;
    }

    public double[][] getJacobian() {
        return jacobian;
    }

    public void setJacobian(double[][] jacobian) {
        this.jacobian = jacobian;
    }

    public double[] getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(double[] deltaX) {
        this.deltaX = deltaX;
    }

    public double[] getNextXValues() {
        return nextXValues;
    }

    public void setNextXValues(double[] nextXValues) {
        this.nextXValues = nextXValues;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }
}

