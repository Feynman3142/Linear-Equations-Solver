package solver;

import java.util.Arrays;

abstract class LinearSystem {

    boolean hasInfiniteSolutions;
    boolean hasNoSolutions;
    boolean hasUniqueSolution;
    Matrix coeffMat;
    Matrix constMat;
    double[] solArr;

    /**
     * Solve the linear system of equations represented by
     * the coefficient matrix ('coeffMat') and the vector of constants ('constMat')
     */
    abstract void solve();
}

class GaussJordSolver extends LinearSystem {
    /*
    Class that extends LinearSystem and uses Gaussian-Jordan elimination
    to solve a linear system of equation
     */

    /**
     * Constructor for the class
     * @param coeffMat the matrix of coefficients in the linear equation system ('Matrix' class)
     * @param constMat the vector of constants in the linear equation system ('Matrix' class)
     */
    GaussJordSolver(Matrix coeffMat, Matrix constMat) {
        this.coeffMat = coeffMat;
        this.constMat = constMat;
        this.hasInfiniteSolutions = false;
        this.hasUniqueSolution = false;
        this.hasNoSolutions = false;
        this.solArr = new double[coeffMat.cols];
    }

    /**
     * Performs the elementary scaling of a matrix's row
     * @param mat the matrix to operate on ('Matrix' class)
     * @param row the row of the matrix to operate on (int)
     * @param scaleFactor the factor by which to scale the row (double)
     */
    private static void scaleRow(Matrix mat, int row, double scaleFactor) {
        // If an invalid row number is given, throw an exception
        if (row >= mat.rows) {
            throw new IllegalArgumentException(String.format("Cannot perform operation on row %d with a matrix of %d rows", row, mat.rows));
        }
        for (int elem = 0; elem < mat.cols; ++elem) {
            mat.data[row][elem] *= scaleFactor;
        }
    }

    /**
     * Performs the elemantary swapping of rows of a matrix
     * @param mat the matrix to operate on ('Matrix' class)
     * @param row1 the first row to swap (int)
     * @param row2 the second row to swap (int)
     */
    private static void swapRows(Matrix mat, int row1, int row2) {
        // If an invalid row number is given, throw an exception
        if (row1 >= mat.rows || row2 >= mat.rows) {
            throw new IllegalArgumentException(String.format("Cannot perform operation on rows %d and %d with a matrix of %d rows", row1, row2, mat.rows));
        }
        double[] tempStore = mat.data[row1];
        mat.data[row1] = mat.data[row2];
        mat.data[row2] = tempStore;
    }

    /**
     * Performs the elementary swapping of columns of a matrix
     * @param mat the matrix to operate on ('Matrix' class)
     * @param col1 the first column to swap (int)
     * @param col2 the second column to swap (int)
     */
    private static void swapCols(Matrix mat, int col1, int col2) {
        // If an invalid column number is given, throw an exception
        if (col1 >= mat.cols || col2 >= mat.cols) {
            throw new IllegalArgumentException(String.format("Cannot perform operation on columns %d and %d with a matrix of %d columns", col1, col2, mat.cols));
        }
        for (int row = 0; row < mat.rows; ++row) {
            double temp = mat.data[row][col1];
            mat.data[row][col1] = mat.data[row][col2];
            mat.data[row][col2] = temp;
        }
    }

    /**
     * Performs the addition of a scaled row to another row
     * Here we do row1 <- row1 + (scaleFactor * row2)
     * @param mat the matrix to operate on ('Matrix' class)
     * @param row1 the row to add to (int)
     * @param row2 the row being added (int)
     * @param scaleFactor the factor by which to scale row2 before addition. (Does not affect row2)
     */
    private static void addScaledRowToRow(Matrix mat, int row1, int row2, double scaleFactor) {
        // If an invalid row number is given, throw an exception
        if (row1 >= mat.rows || row2 >= mat.rows) {
            throw new IllegalArgumentException(String.format("Cannot perform operation on rows %d and %d with a matrix of %d rows", row1, row2, mat.rows));
        }
        for (int elem = 0; elem < mat.cols; ++elem) {
            mat.data[row1][elem] += (scaleFactor * mat.data[row2][elem]);
        }
    }

    /**
     * Finds the first non-zero element in the matrix given a row and column
     * It first checks non-zero elements in the same column below the row specified
     * Then it checks towards right of the element in the row specified
     * If still not found, it proceeds to the diagonally next element
     * And repeats the above steps till it is found or not
     * @param mat the matrix to operate on ('Matrix' class)
     * @param row the starting row to search from (int)
     * @param col the starting column to search from (int)
     * @return the index as an int[] of the first non-zero element starting from MAT(row, col)
     * and proceeding bottom right.
     */
    private int[] findNonZeroElem(Matrix mat, int row, int col) {
        // If an invalid row number is given, throw an exception
        if (row >= mat.rows || col >= mat.cols) {
            throw new IllegalArgumentException(String.format("Cannot perform operation on row %d and column %d with a matrix %dx%d", row, col, mat.rows, mat.cols));
        }
        boolean isFound = false;
        int[] inds = null;
        while (row < mat.rows && col < mat.cols && !isFound) {
            // Adding 'row' as argument to ensure that we always search below the element
            // So it is used as an offset basically
            int rowInd = findNonZeroElemInCol(mat, col, row);
            if (rowInd == -1) {
                // Adding 'col' as argument to ensure that we always search to the right of the element
                // So it is used as an offset basically
                int colInd = findNonZeroElemInRow(mat, row, col);
                // 'colInd == mat.cols - 1' ensures that we're not using the
                // column of the constant vector in the augmented matrix
                if (colInd == -1 || colInd == mat.cols - 1) {
                    ++row;
                    ++col;
                } else {
                    inds = new int[]{row, colInd};
                    isFound = true;
                }
            } else {
                inds = new int[]{rowInd, col};
                isFound = true;
            }
        }
        return inds;
    }

    /**
     * Finds the first non-zero element in the row specified
     * @param mat the matrix to operate on ('Matrix' class)
     * @param row the row of the matrix to search for (int)
     * @return the index (int) of the first non-zero element
     */
    private int findNonZeroElemInRow(Matrix mat, int row, int colOffset) {
        // If an invalid row number is given, throw an exception
        if (row >= mat.rows || colOffset >= mat.cols) {
            throw new IllegalArgumentException(String.format("Cannot perform operation on row %d and column %d with a matrix of %d x %d", row, colOffset, mat.rows, mat.cols));
        }
        for (int col = colOffset; col < mat.cols; ++col) {
            // using this criteria to determine if an element is zero or not due to rounding errors
            // that can cause a number not to be zero exactly and thus give a false positive
            if (Math.abs(mat.data[row][col]) > 0.001) {
                return col;
            }
        }
        return -1;
    }

    /**
     * Finds the first non-zero element in the column specified
     * @param mat the matrix to operate on ('Matrix' class)
     * @param col the column of the matrix to search for (int)
     * @return the index (int) of the first non-zero element
     */
    private int findNonZeroElemInCol(Matrix mat, int col, int rowOffset) {
        // If an invalid row number is given, throw an exception
        if (col >= mat.cols || rowOffset >= mat.rows) {
            throw new IllegalArgumentException(String.format("Cannot perform operation on row %d and column %d with a matrix of %d x %d", rowOffset, col, mat.rows, mat.cols));
        }
        for (int row = rowOffset; row < mat.rows; ++row) {
            // using this criteria to determine if an element is zero or not due to rounding errors
            // that can cause a number not to be zero exactly and thus give a false positive
            if (Math.abs(mat.data[row][col]) > 0.001) {
                return row;
            }
        }
        return -1;
    }

    @Override
    void solve() {

        // Create the system matrix (augmented matrix)
        Matrix sysMat = Matrix.createMatrix(coeffMat.rows, coeffMat.cols + 1);
        for (int row = 0; row < sysMat.rows; ++row) {
            System.arraycopy(coeffMat.data[row], 0, sysMat.data[row], 0, coeffMat.cols);
            sysMat.data[row][sysMat.cols - 1] = constMat.data[row][0];
        }

        // Create an array to keep track of column swaps
        // The array has a size equal to the # of columns in the coefficient matrix
        // When two columns C1 & C2 are swapped, we track it like colSwapHistory[max(C1, C2)] = min(C1, C2)
        // Then when we have to undo the swaps, we traverse from the end of array and unswap the columns
        // This works because column swapping is only done between columns to the right of a particular element
        // So when we traverse from the end, we are undoing the latest swaps to the earliest ones
        int[] colSwapHistory = new int[coeffMat.cols];
        Arrays.fill(colSwapHistory, -1);

        for (int row = 0; row < Math.min(coeffMat.cols, coeffMat.rows); ++row) {
            // inds[0], inds[1] -> row #, col # of first non-zero element
            int[] inds = findNonZeroElem(sysMat, row, row);
            // If inds is null, it means we couldn't find a non-zero element and can quit this stage of the algo
            if (inds == null) {
                break;
            }
            // If non-zero element was found in a different row, swap the rows
            if (inds[0] != row) {
                swapRows(sysMat, inds[0], row);
                System.out.printf("R%d <-> R%d\n", row + 1, inds[0] + 1);
            }
            // If non-zero element was found in a different column, swap the columns
            if (inds[1] != row) {
                swapCols(sysMat, inds[1], row);
                System.out.printf("C%d <-> C%d\n", row + 1, inds[1] + 1);
                colSwapHistory[Math.max(inds[1], row)] = Math.min(inds[1], row); // Look at comment above 'colSwapHistory' declaration for more
            }
            // Scale non-zero element to 1; skip if it already is
            if (sysMat.data[row][row] != 1.0) {
                double scaleFactor = 1.0 / sysMat.data[row][row];
                scaleRow(sysMat, row, scaleFactor);
                System.out.printf("%f * R%d -> R%d\n", scaleFactor, row + 1, row + 1);
            }
            // Make each element in the same column below the row equal to zero; skip if already is
            for (int otherRow = row + 1; otherRow < sysMat.rows; ++otherRow) {
                if (sysMat.data[otherRow][row] != 0.0) {
                    double otherScaleFactor = (-1) * sysMat.data[otherRow][row];
                    addScaledRowToRow(sysMat, otherRow, row, otherScaleFactor);
                    System.out.printf("%f * R%d + R%d -> R%d\n", otherScaleFactor, row + 1, otherRow + 1, otherRow + 1);
                }
            }
        }
        // Now check for # of significant equations ( == # of rows with non-zero elements
        int numSignificantVars = coeffMat.cols;
        int numSignificantEqns = 0;
        for (int row = 0; row < sysMat.rows; ++row) {
            int ind = findNonZeroElemInRow(sysMat, row, 0);

            // If we find that a non-zero element only exists in the last column of the
            // augmented matrix there is a contradiction and the linear system has no solutions
            if (ind == sysMat.cols - 1) {
                hasNoSolutions = true;
                break;
            // As long as there exists a non-zero element in the row, it is counted towards the # of significant equations
            } else if (ind != -1) {
                ++numSignificantEqns;
            }
        }

        if (hasNoSolutions) {
            hasInfiniteSolutions = false;
            hasUniqueSolution = false;
        // Criteria for infinite solutions is as below
        } else if (numSignificantEqns < numSignificantVars) {
            hasInfiniteSolutions = true;
            hasNoSolutions = false;
            hasUniqueSolution = false;
        // Here # of significant equations == # of significant variables
        // So there is an unique solution
        } else {
            // Convert upper triangular matrix to reduced row echelon form
            for (int row = 0; row < numSignificantEqns; ++row) {
                for (int col = row + 1; col < sysMat.cols - 1; ++col) {
                    if (sysMat.data[row][col] != 0.0) {
                        double scaleFactor = (-1) * sysMat.data[row][col];
                        addScaledRowToRow(sysMat, row, col, scaleFactor);
                        System.out.printf("%f * R%d -> R%d\n", scaleFactor, col + 1, row + 1);
                    }
                }
            }

            // Undo the swaps made to the columns so that the right
            // variables in the system are assigned to the right values
            for (int col = colSwapHistory.length - 1; col >= 0; --col) {
                if (colSwapHistory[col] != -1) {
                    swapCols(sysMat, col, colSwapHistory[col]);
                }
            }

            // Now assign the right variable to the right value
            for (int row = 0; row < numSignificantVars; ++row) {
                // We can't just start from MAT(0, 0) because the column swapping
                // might have shifted the position of the 1 (the leading entry)
                // So we find the position of the 1 and use that to index solArr
                int ind = findNonZeroElemInRow(sysMat, row, 0);
                solArr[ind] = sysMat.data[row][sysMat.cols - 1];
            }

            hasUniqueSolution = true;
            hasNoSolutions = false;
            hasInfiniteSolutions = false;
        }
    }
}

