package solver;

class Matrix {
    /*
    Class to create and perform operations on matrices
     */
    int rows; // # of rows of matrix
    int cols; // # of columns of matrix
    double[][] data; // actual data of the matrix

    /**
     * Constructor for the Matrix class
     * @param rows # of rows of matrix (int)
     * @param cols # of columns of matrix (int)
     * @param data data of the matrix (double[][])
     */
    private Matrix(int rows, int cols, double[][] data) {
        this.rows = rows;
        this.cols = cols;
        this.data = data;
    }

    /**
     * Static method to create Matrix instances
     * @param data data of the matrix (double[][])
     * @return an instance of Matrix class initialised with data[][]
     */
    static Matrix createMatrix(double[][] data) {
        int rows = data.length;
        int cols = data[0].length;
        for (double[] row : data) {
            if (row.length != cols) {
                throw new IllegalArgumentException("2-D array given does not have uniform size!");
            }
        }
        return new Matrix(rows, cols, data);
    }

    static Matrix createMatrix(int rows, int cols) {
        if ((rows < 1) || (cols < 1)) {
            throw new IllegalArgumentException(String.format("Invalid dimensions for matrix: %dx%d", rows, cols));
        }
        return new Matrix(rows, cols, new double[rows][cols]);
    }
}
