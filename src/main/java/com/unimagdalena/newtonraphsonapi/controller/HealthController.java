package com.unimagdalena.newtonraphsonapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health check endpoint for the Newton-Raphson API.
 *
 * Provides a simple status endpoint useful for load balancers, monitoring systems,
 * and deployment orchestration tools to verify the service is running.
 *
 * @author Newton-Raphson API
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    /**
     * Health check endpoint that returns the current status of the service.
     *
     * @return ResponseEntity containing a map with status information
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> healthStatus = Map.of(
            "status", "UP",
            "service", "newton-raphson-api"
        );
        return new ResponseEntity<>(healthStatus, HttpStatus.OK);
    }
}

