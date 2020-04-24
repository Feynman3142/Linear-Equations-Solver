package solver;

import java.io.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        File inFile = null;
        File outFile = null;

        for (int ind = 0; ind < args.length; ++ind) {
            if ("-in".equals(args[ind])) {
                inFile = new File(args[ind + 1]);
            } else if ("-out".equals(args[ind])) {
                outFile = new File(args[ind + 1]);
            }
        }

        if (inFile == null) {
            System.out.println("No input file specified!");
            return;
        }
        if (outFile == null) {
            System.out.println("No output file specified!");
            return;
        }

        Matrix coeffMat = null;
        Matrix constMat = null;
        boolean gotData = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inFile))) {
            try {
                // size[0], size[1] -> # of variables, # of equations
                int[] size = Arrays.stream(reader.readLine().split("\\s+")).mapToInt(Integer::parseInt).toArray();
                int numVars = size[0];
                int numEqns = size[1];
                coeffMat = Matrix.createMatrix(numEqns, numVars);
                constMat = Matrix.createMatrix(numEqns, 1);
                int row = 0;
                while (reader.ready()) {
                    // Friendly note: use the argument in toArray() to cast to a user class array instead of explicit class casting
                    Complex[] vals = Arrays.stream(reader.readLine().split("\\s+")).map(Complex::parseComplex).toArray(Complex[]::new);
                    constMat.data[row][0] = vals[numVars];
                    System.arraycopy(vals, 0, coeffMat.data[row], 0, numVars);
                    ++row;
                }
                gotData = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid data found in input file. Unable to parse!");
                System.out.printf("[DETAILS]\n%s", e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            System.out.printf("Could not find input file: %s", inFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!gotData) {
            return;
        }

        System.out.println("Start solving the equation.\n");

        LinearSystem linSys = new GaussJordSolver(coeffMat, constMat);
        linSys.solve();
        if (linSys.hasInfiniteSolutions) {
            System.out.println("\nInfinitely many solutions");
        } else if (linSys.hasNoSolutions) {
            System.out.println("\nNo solutions");
        } else if (linSys.hasUniqueSolution){
            System.out.print("\nThe solution is: (");
            for (int elem = 0; elem < linSys.solArr.length; ++elem) {
                System.out.printf("%s%s", linSys.solArr[elem].toString(), (elem == linSys.solArr.length - 1) ? ")\n" : ", ");
            }
        } else {
            System.out.println("\nError determining solution");
        }

        if (!outFile.exists() && !outFile.createNewFile()) {
                System.out.printf("Could not create output file: %s\n", outFile.getAbsolutePath());
        } else {
            try (PrintWriter writer = new PrintWriter(outFile)) {
                if (linSys.hasInfiniteSolutions) {
                    writer.println("Infinitely many solutions");
                } else if (linSys.hasNoSolutions) {
                    writer.println("No solutions");
                } else if (linSys.hasUniqueSolution) {
                    for (Complex val : linSys.solArr) {
                        writer.println(val);
                    }
                } else {
                    writer.println("Error determining solution");
                }
                System.out.printf("\nSaved to %s\n", outFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
