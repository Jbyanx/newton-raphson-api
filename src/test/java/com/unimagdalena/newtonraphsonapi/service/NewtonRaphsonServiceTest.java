package com.unimagdalena.newtonraphsonapi.service;

import com.unimagdalena.newtonraphsonapi.exception.EquationParseException;
import com.unimagdalena.newtonraphsonapi.model.request.SolveRequest;
import com.unimagdalena.newtonraphsonapi.model.response.IterationStep;
import com.unimagdalena.newtonraphsonapi.model.response.SolveResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NewtonRaphsonService.
 *
 * Tests the solve() method with various linear and nonlinear equation systems,
 * covering normal convergence, edge cases, and error conditions.
 *
 * @author Newton-Raphson API Test Suite
 * @version 1.0
 */
@DisplayName("NewtonRaphsonService Tests")
class NewtonRaphsonServiceTest {

    private static final double DELTA = 1e-6;

    private NewtonRaphsonService service;

    @BeforeEach
    void setUp() {
        service = new NewtonRaphsonService();
    }

    /**
     * Test case a: Solve a simple linear system.
     *
     * System:
     * x + y = 4
     * x - y = 2
     *
     * Solution: x = 3.0, y = 1.0
     */
    @Test
    @DisplayName("shouldConvergeForSimpleLinearSystem")
    void shouldConvergeForSimpleLinearSystem() {
        SolveRequest request = new SolveRequest(
            List.of("x + y - 4", "x - y - 2"),
            List.of("x", "y"),
            List.of(0.0, 0.0)
        );

        SolveResponse response = service.solve(request);

        assertTrue(response.isConverged(), "Should converge for simple linear system");
        assertEquals(3.0, response.getSolution().get("x"), DELTA, "x should be approximately 3.0");
        assertEquals(1.0, response.getSolution().get("y"), DELTA, "y should be approximately 1.0");
    }

    /**
     * Test case b: Solve a nonlinear system.
     *
     * System:
     * x^2 + y - 4 = 0
     * x + y^2 - 6 = 0
     *
     * Initial guess: [1.0, 1.0]
     */
    @Test
    @DisplayName("shouldConvergeForNonlinearSystem")
    void shouldConvergeForNonlinearSystem() {
        SolveRequest request = new SolveRequest(
            List.of("x^2 + y - 4", "x + y^2 - 6"),
            List.of("x", "y"),
            List.of(1.0, 1.0)
        );

        SolveResponse response = service.solve(request);

        assertTrue(response.isConverged(), "Should converge for nonlinear system");
        assertTrue(response.getTotalIterations() > 0, "Should have at least one iteration");
        assertFalse(response.getSteps().isEmpty(), "Steps list should not be empty");
        assertTrue(Double.isFinite(response.getSolution().get("x")), "x solution should be finite");
        assertTrue(Double.isFinite(response.getSolution().get("y")), "y solution should be finite");
    }

    /**
     * Test case c: Verify the structure of IterationStep objects in the response.
     *
     * For each step, verify that all array fields have the correct dimensions
     * and that error values are non-negative.
     */
    @Test
    @DisplayName("shouldReturnStepsWithCorrectStructure")
    void shouldReturnStepsWithCorrectStructure() {
        SolveRequest request = new SolveRequest(
            List.of("x^2 + y - 4", "x + y^2 - 6"),
            List.of("x", "y"),
            List.of(1.0, 1.0)
        );

        SolveResponse response = service.solve(request);

        assertFalse(response.getSteps().isEmpty(), "Steps list should not be empty");

        for (IterationStep step : response.getSteps()) {
            assertEquals(2, step.getXValues().length, "xValues should have length 2");
            assertEquals(2, step.getFValues().length, "fValues should have length 2");
            assertEquals(2, step.getJacobian().length, "jacobian should have 2 rows");
            assertEquals(2, step.getJacobian()[0].length, "jacobian should have 2 columns");
            assertEquals(2, step.getDeltaX().length, "deltaX should have length 2");
            assertEquals(2, step.getNextXValues().length, "nextXValues should have length 2");
            assertTrue(step.getError() >= 0, "error should be non-negative");
        }
    }

    /**
     * Test case d: Verify that invalid expressions throw EquationParseException.
     *
     * Using malformed mathematical expressions with invalid operators ($$ and **).
     */
    @Test
    @DisplayName("shouldThrowEquationParseExceptionForInvalidExpression")
    void shouldThrowEquationParseExceptionForInvalidExpression() {
        SolveRequest request = new SolveRequest(
            List.of("x ** y + garbage$$", "x + y"),
            List.of("x", "y"),
            List.of(1.0, 1.0)
        );

        assertThrows(EquationParseException.class, () -> {
            service.solve(request);
        }, "Should throw EquationParseException for invalid expression");
    }

    /**
     * Test case e: Verify behavior when maxIterations is set to 1.
     *
     * With only one iteration allowed, the algorithm should not converge
     * but should return the best approximation found.
     */
    @Test
    @DisplayName("shouldNotConvergeAndReturnLastApproximation")
    void shouldNotConvergeAndReturnLastApproximation() {
        SolveRequest request = new SolveRequest(
            List.of("x^2 + y - 4", "x + y^2 - 6"),
            List.of("x", "y"),
            List.of(1.0, 1.0)
        );
        request.setMaxIterations(1);

        SolveResponse response = service.solve(request);

        assertFalse(response.isConverged(), "Should not converge with maxIterations=1");
        assertEquals(1, response.getTotalIterations(), "Should have exactly 1 iteration");
        assertEquals(1, response.getSteps().size(), "Steps list should contain 1 step");
    }
}

