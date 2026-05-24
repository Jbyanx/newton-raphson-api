package com.unimagdalena.newtonraphsonapi.util;

import com.unimagdalena.newtonraphsonapi.exception.SingularMatrixException;

/**
 * Solves a 2x2 linear system Ax = b using Gaussian elimination with partial
 * pivoting. Used internally by NewtonRaphsonService to compute the correction
 * vector Δx at each iteration. Partial pivoting improves numerical stability
 * by avoiding division by small pivot values.
 *
 * @author Newton-Raphson API
 * @version 1.0
 */
public class GaussianElimination {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private GaussianElimination() {
    }

    /**
     * Solves the linear system Ax = b using Gaussian elimination with partial pivoting.
     *
     * The method operates on copies of the input matrices, leaving the originals unchanged.
     *
     * @param A the 2x2 coefficient matrix. Must not be null.
     * @param b the right-hand side vector of length 2. Must not be null.
     * @return the solution vector x of length 2
     * @throws SingularMatrixException if the matrix is singular or near-singular
     *                                  (|pivot| < 1e-12 at the last step)
     */
    public static double[] solve(double[][] A, double[] b) {
        int n = 2;

        // Step 1: Build augmented matrix [A|b] of size n x (n+1)
        double[][] augmented = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, augmented[i], 0, n);
            augmented[i][n] = b[i];
        }

        // Step 2: Forward elimination with partial pivoting
        for (int k = 0; k < n; k++) {
            // Step 2a: Find the row with the maximum absolute value in column k (from row k downward)
            int maxRow = k;
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(augmented[i][k]) > Math.abs(augmented[maxRow][k])) {
                    maxRow = i;
                }
            }

            // Step 2b: Swap that row with row k in the augmented matrix
            double[] temp = augmented[k];
            augmented[k] = augmented[maxRow];
            augmented[maxRow] = temp;

            // Step 2c: For each row i below k
            for (int i = k + 1; i < n; i++) {
                double factor = augmented[i][k] / augmented[k][k];
                for (int j = k; j <= n; j++) {
                    augmented[i][j] -= factor * augmented[k][j];
                }
            }
        }

        // Step 3: Check for singular matrix
        if (Math.abs(augmented[n - 1][n - 1]) < 1e-12) {
            throw new SingularMatrixException(
                "Jacobian matrix is singular at this iteration. The system may have no solution or Newton-Raphson diverged."
            );
        }

        // Step 4: Back substitution
        double[] result = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            result[i] = augmented[i][n];
            for (int j = i + 1; j < n; j++) {
                result[i] -= augmented[i][j] * result[j];
            }
            result[i] /= augmented[i][i];
        }

        // Step 5: Return result array
        return result;
    }
}

