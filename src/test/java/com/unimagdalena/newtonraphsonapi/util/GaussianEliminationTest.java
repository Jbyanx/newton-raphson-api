package com.unimagdalena.newtonraphsonapi.util;

import com.unimagdalena.newtonraphsonapi.exception.SingularMatrixException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GaussianElimination utility class.
 *
 * Tests the static solve() method with various 2x2 linear systems,
 * including cases that require partial pivoting and singular matrices.
 *
 * @author Newton-Raphson API Test Suite
 * @version 1.0
 */
@DisplayName("GaussianElimination Tests")
class GaussianEliminationTest {

    private static final double DELTA = 1e-9;

    /**
     * Test case a: Solve a simple 2x2 system without requiring pivoting.
     *
     * System:
     * 2x + 1y = 5
     * 1x + 3y = 10
     *
     * Expected solution: x = 1.0, y = 3.0
     */
    @Test
    @DisplayName("shouldSolveSimpleSystem")
    void shouldSolveSimpleSystem() {
        double[][] A = {{2, 1}, {1, 3}};
        double[] b = {5, 10};

        double[] result = GaussianElimination.solve(A, b);

        assertEquals(1.0, result[0], DELTA, "x should be 1.0");
        assertEquals(3.0, result[1], DELTA, "y should be 3.0");
    }

    /**
     * Test case b: Solve a system that requires partial pivoting.
     *
     * System:
     * 0x + 1y = 3
     * 1x + 0y = 7
     *
     * This system has a zero in the pivot position, forcing partial pivoting
     * to swap rows.
     *
     * Expected solution: x = 7.0, y = 3.0
     */
    @Test
    @DisplayName("shouldSolveSystemWithPivoting")
    void shouldSolveSystemWithPivoting() {
        double[][] A = {{0, 1}, {1, 0}};
        double[] b = {3, 7};

        double[] result = GaussianElimination.solve(A, b);

        assertEquals(7.0, result[0], DELTA, "x should be 7.0");
        assertEquals(3.0, result[1], DELTA, "y should be 3.0");
    }

    /**
     * Test case c: Attempt to solve a singular (non-invertible) system.
     *
     * System:
     * 1x + 2y = 5
     * 2x + 4y = 10
     *
     * The second row is 2 times the first row, making the matrix singular.
     *
     * Expected: throws SingularMatrixException
     */
    @Test
    @DisplayName("shouldThrowSingularMatrixException")
    void shouldThrowSingularMatrixException() {
        double[][] A = {{1, 2}, {2, 4}};
        double[] b = {5, 10};

        assertThrows(SingularMatrixException.class, () -> {
            GaussianElimination.solve(A, b);
        }, "Should throw SingularMatrixException for singular matrix");
    }
}

