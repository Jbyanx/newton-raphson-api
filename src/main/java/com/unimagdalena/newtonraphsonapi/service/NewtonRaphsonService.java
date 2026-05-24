package com.unimagdalena.newtonraphsonapi.service;

import com.unimagdalena.newtonraphsonapi.exception.EquationParseException;
import com.unimagdalena.newtonraphsonapi.model.request.SolveRequest;
import com.unimagdalena.newtonraphsonapi.model.response.IterationStep;
import com.unimagdalena.newtonraphsonapi.model.response.SolveResponse;
import com.unimagdalena.newtonraphsonapi.util.GaussianElimination;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Service class implementing the Newton-Raphson method for solving 2x2
 * nonlinear equation systems.
 *
 * This service uses exp4j for mathematical expression parsing and evaluation.
 * It computes the Jacobian matrix numerically using finite differences and
 * solves the linear system Ax = b at each iteration using Gaussian elimination.
 *
 * @author Newton-Raphson API
 * @version 1.0
 */
@Service
public class NewtonRaphsonService {

    /**
     * Evaluates a mathematical expression at a given point.
     *
     * Uses exp4j to build and evaluate the expression with the provided variable
     * values. The expression should be a valid mathematical string containing the
     * variable names specified in the variables list.
     *
     * @param expression the mathematical expression as a string (e.g., "x^2 + y - 4")
     * @param variables the list of variable names (e.g., ["x", "y"])
     * @param point the array of values corresponding to each variable
     * @return the evaluated result of the expression
     * @throws EquationParseException if the expression cannot be parsed or evaluated
     */
    private double evaluateFunction(String expression, List<String> variables, double[] point) {
        try {
            ExpressionBuilder builder = new ExpressionBuilder(expression);
            for (int i = 0; i < variables.size(); i++) {
                builder.variable(variables.get(i));
            }
            Expression expr = builder.build();
            for (int i = 0; i < variables.size(); i++) {
                expr.setVariable(variables.get(i), point[i]);
            }
            return expr.evaluate();
        } catch (Exception e) {
            throw new EquationParseException(
                "Failed to evaluate expression: '" + expression + "' — " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Computes the 2x2 Jacobian matrix numerically using forward finite differences.
     *
     * For each partial derivative ∂fi/∂xj, uses the formula:
     * J[i][j] ≈ (f_i(x + h*e_j) - f_i(x)) / h
     * where e_j is the unit vector in the j-th direction (h = 1e-7).
     *
     * This numerical approach avoids the need for symbolic differentiation.
     *
     * @param equations the list of 2 equations
     * @param variables the list of 2 variable names
     * @param point the point at which to compute the Jacobian
     * @return a 2x2 Jacobian matrix
     */
    private double[][] computeJacobian(List<String> equations, List<String> variables, double[] point) {
        double h = 1e-7;
        double[][] jacobian = new double[2][2];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                double[] perturbedPoint = Arrays.copyOf(point, point.length);
                perturbedPoint[j] += h;
                double fPerturbation = evaluateFunction(equations.get(i), variables, perturbedPoint);
                double fOriginal = evaluateFunction(equations.get(i), variables, point);
                jacobian[i][j] = (fPerturbation - fOriginal) / h;
            }
        }

        return jacobian;
    }

    /**
     * Evaluates all equations of the system at a given point.
     *
     * Computes the function vector F(x) = [f1(x), f2(x)].
     *
     * @param equations the list of 2 equations
     * @param variables the list of 2 variable names
     * @param point the point at which to evaluate the system
     * @return a double array [f1(x), f2(x)]
     */
    private double[] evaluateSystem(List<String> equations, List<String> variables, double[] point) {
        double[] fValues = new double[2];
        fValues[0] = evaluateFunction(equations.get(0), variables, point);
        fValues[1] = evaluateFunction(equations.get(1), variables, point);
        return fValues;
    }

    /**
     * Solves a 2x2 system of nonlinear equations using the Newton-Raphson method.
     *
     * The algorithm iteratively refines the solution approximation by:
     * 1. Evaluating the system F(x) at the current point
     * 2. Computing the Jacobian matrix J numerically
     * 3. Solving the linear system J·Δx = -F for the correction vector
     * 4. Updating x = x + Δx
     * 5. Checking convergence: if |Δx_max| < tolerance, the solution has converged
     *
     * The response includes the complete iteration history for analysis and debugging.
     *
     * @param request the SolveRequest containing equations, variables, initial guess,
     *                and convergence parameters
     * @return a SolveResponse with the solution, convergence status, and iteration history
     * @throws EquationParseException if any equation cannot be parsed or evaluated
     */
    public SolveResponse solve(SolveRequest request) {
        // Step 1: Extract parameters, using defaults if null
        List<String> equations = request.getEquations();
        List<String> variables = request.getVariables();
        Integer maxIterationsParam = request.getMaxIterations();
        Double toleranceParam = request.getTolerance();

        int maxIterations = (maxIterationsParam != null) ? maxIterationsParam : 100;
        double tolerance = (toleranceParam != null) ? toleranceParam : 1e-7;

        // Step 2: Initialize variables
        double[] x = new double[2];
        List<Double> initialGuess = request.getInitialGuess();
        x[0] = initialGuess.get(0);
        x[1] = initialGuess.get(1);

        List<IterationStep> steps = new ArrayList<>();
        boolean converged = false;
        double finalError = Double.MAX_VALUE;
        int iterationCount = 0;

        // Step 3: Validate equations can be parsed before starting the loop
        evaluateSystem(equations, variables, x);

        // Step 4: Iteration loop
        for (int iter = 0; iter < maxIterations; iter++) {
            // Step 4a: Evaluate F(x)
            double[] fValues = evaluateSystem(equations, variables, x);

            // Step 4b: Compute Jacobian
            double[][] jacobian = computeJacobian(equations, variables, x);

            // Step 4c: Compute -F(x)
            double[] negF = new double[]{-fValues[0], -fValues[1]};

            // Step 4d: Solve J·Δx = -F using Gaussian Elimination
            double[] deltaX = GaussianElimination.solve(jacobian, negF);

            // Step 4e: Compute nextX
            double[] nextX = new double[]{x[0] + deltaX[0], x[1] + deltaX[1]};

            // Step 4f: Compute error
            double error = Math.max(Math.abs(deltaX[0]), Math.abs(deltaX[1]));

            // Step 4g: Build IterationStep with all fields populated
            IterationStep step = new IterationStep(
                iter + 1,
                Arrays.copyOf(x, x.length),
                Arrays.copyOf(fValues, fValues.length),
                copyMatrix(jacobian),
                Arrays.copyOf(deltaX, deltaX.length),
                Arrays.copyOf(nextX, nextX.length),
                error
            );
            steps.add(step);

            // Step 4h: Update x
            x = nextX;

            // Step 4i: Update iterationCount
            iterationCount = iter + 1;

            // Step 4j: Update finalError
            finalError = error;

            // Step 4k: Check convergence
            if (error < tolerance) {
                converged = true;
                break;
            }
        }

        // Step 5: Build and return SolveResponse
        LinkedHashMap<String, Double> solution = new LinkedHashMap<>();
        solution.put(variables.get(0), roundToTenDecimals(x[0]));
        solution.put(variables.get(1), roundToTenDecimals(x[1]));

        return new SolveResponse(
            converged,
            variables,
            solution,
            iterationCount,
            finalError,
            steps
        );
    }

    /**
     * Helper method to create a deep copy of a 2x2 matrix.
     *
     * @param matrix the 2x2 matrix to copy
     * @return a new 2x2 matrix with the same values
     */
    private double[][] copyMatrix(double[][] matrix) {
        double[][] copy = new double[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return copy;
    }

    /**
     * Rounds a double value to 10 decimal places using HALF_UP rounding mode.
     *
     * @param value the value to round
     * @return the rounded value
     */
    private double roundToTenDecimals(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(10, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}


