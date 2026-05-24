package com.unimagdalena.newtonraphsonapi.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for HealthController using MockMvc.
 *
 * Tests the health check endpoint that is used by load balancers and
 * monitoring systems.
 *
 * @author Newton-Raphson API Test Suite
 * @version 1.0
 */
@DisplayName("HealthController Tests")
@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test case e: Verify health endpoint returns correct status.
     *
     * GET /api/v1/health should return HTTP 200 with status "UP"
     * and service name "newton-raphson-api"
     */
    @Test
    @DisplayName("shouldReturn200ForHealthEndpoint")
    void shouldReturn200ForHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", equalTo("UP")))
            .andExpect(jsonPath("$.service", equalTo("newton-raphson-api")));
    }
}

