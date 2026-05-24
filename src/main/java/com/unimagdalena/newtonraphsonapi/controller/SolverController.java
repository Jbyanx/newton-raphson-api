package com.unimagdalena.newtonraphsonapi.controller;

import com.unimagdalena.newtonraphsonapi.model.request.SolveRequest;
import com.unimagdalena.newtonraphsonapi.model.response.SolveResponse;
import com.unimagdalena.newtonraphsonapi.service.NewtonRaphsonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for the Newton-Raphson equation solver API.
 *
 * Exposes endpoints for solving 2x2 nonlinear equation systems using the
 * Newton-Raphson iterative method.
 *
 * @author Newton-Raphson API
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1")
public class SolverController {

    private final NewtonRaphsonService newtonRaphsonService;

    /**
     * Constructs the SolverController with dependency injection.
     *
     * @param newtonRaphsonService the Newton-Raphson solver service
     */
    public SolverController(NewtonRaphsonService newtonRaphsonService) {
        this.newtonRaphsonService = newtonRaphsonService;
    }

    /**
     * Solves a 2x2 system of nonlinear equations using the Newton-Raphson method.
     *
     * This endpoint accepts a SolveRequest containing:
     * - Two mathematical expressions (equations)
     * - Two variable names
     * - An initial guess vector
     * - Convergence parameters (optional: maxIterations, tolerance)
     *
     * Returns a SolveResponse containing:
     * - The computed solution
     * - Convergence status
     * - Complete iteration history
     *
     * The request body is validated according to SolveRequest constraints.
     * Any validation errors result in HTTP 400 with detailed error messages.
     *
     * @param request the SolveRequest with equations, variables, and initial guess
     * @return ResponseEntity<SolveResponse> with HTTP 200 and the computed solution
     */
    @PostMapping("/solve")
    public ResponseEntity<SolveResponse> solve(@Valid @RequestBody SolveRequest request) {
        SolveResponse response = newtonRaphsonService.solve(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

