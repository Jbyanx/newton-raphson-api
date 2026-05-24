package com.unimagdalena.newtonraphsonapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimagdalena.newtonraphsonapi.exception.EquationParseException;
import com.unimagdalena.newtonraphsonapi.model.request.SolveRequest;
import com.unimagdalena.newtonraphsonapi.model.response.SolveResponse;
import com.unimagdalena.newtonraphsonapi.service.NewtonRaphsonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for SolverController using MockMvc.
 *
 * Tests the REST endpoint behavior with various valid and invalid requests,
 * including validation error handling and service exception propagation.
 *
 * @author Newton-Raphson API Test Suite
 * @version 1.0
 */
@DisplayName("SolverController Tests")
@WebMvcTest(SolverController.class)
class SolverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NewtonRaphsonService newtonRaphsonService;

    /**
     * Test case a: Verify successful request handling with HTTP 200 response.
     *
     * Mocks the service to return a converged solution and validates the
     * response status and JSON content.
     */
    @Test
    @DisplayName("shouldReturn200WhenRequestIsValid")
    void shouldReturn200WhenRequestIsValid() throws Exception {
        // Arrange
        SolveRequest request = new SolveRequest(
            List.of("x + y - 4", "x - y - 2"),
            List.of("x", "y"),
            List.of(0.0, 0.0)
        );

        Map<String, Double> solution = new LinkedHashMap<>();
        solution.put("x", 3.0);
        solution.put("y", 1.0);

        SolveResponse mockResponse = new SolveResponse(
            true,
            List.of("x", "y"),
            solution,
            3,
            1e-10,
            List.of()
        );

        when(newtonRaphsonService.solve(any(SolveRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.converged", equalTo(true)))
            .andExpect(jsonPath("$.solution.x", equalTo(3.0)));
    }

    /**
     * Test case b: Verify validation error for incorrect equations list size.
     *
     * POST with only 1 equation instead of the required 2.
     * Expected: HTTP 400 with VALIDATION_ERROR
     */
    @Test
    @DisplayName("shouldReturn400WhenEquationsListHasWrongSize")
    void shouldReturn400WhenEquationsListHasWrongSize() throws Exception {
        // Arrange
        Map<String, Object> invalidRequest = new java.util.HashMap<>();
        invalidRequest.put("equations", List.of("x + y - 4"));  // Only 1 equation
        invalidRequest.put("variables", List.of("x", "y"));
        invalidRequest.put("initialGuess", List.of(0.0, 0.0));

        // Act & Assert
        mockMvc.perform(post("/api/v1/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", equalTo("VALIDATION_ERROR")));
    }

    /**
     * Test case c: Verify validation error when initialGuess is missing.
     *
     * POST without the required initialGuess field.
     * Expected: HTTP 400
     */
    @Test
    @DisplayName("shouldReturn400WhenInitialGuessIsNull")
    void shouldReturn400WhenInitialGuessIsNull() throws Exception {
        // Arrange
        Map<String, Object> invalidRequest = new java.util.HashMap<>();
        invalidRequest.put("equations", List.of("x + y - 4", "x - y - 2"));
        invalidRequest.put("variables", List.of("x", "y"));
        // initialGuess is omitted

        // Act & Assert
        mockMvc.perform(post("/api/v1/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    /**
     * Test case d: Verify exception handling for EquationParseException.
     *
     * Service throws EquationParseException, which should be caught by the
     * global exception handler and return HTTP 422.
     */
    @Test
    @DisplayName("shouldReturn422WhenServiceThrowsEquationParseException")
    void shouldReturn422WhenServiceThrowsEquationParseException() throws Exception {
        // Arrange
        SolveRequest request = new SolveRequest(
            List.of("x + y - 4", "x - y - 2"),
            List.of("x", "y"),
            List.of(0.0, 0.0)
        );

        when(newtonRaphsonService.solve(any(SolveRequest.class)))
            .thenThrow(new EquationParseException("Failed to evaluate expression", null));

        // Act & Assert
        mockMvc.perform(post("/api/v1/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error", equalTo("EQUATION_PARSE_ERROR")));
    }
}

