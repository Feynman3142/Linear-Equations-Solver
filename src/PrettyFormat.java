package solver;

public class PrettyFormat {
    /*
    Class to round a double upto 'n' decimal places and then
    prints them in a clean format, removing trailing zeros
    and even the decimal point if it can be done without
    losing information
     */
    private int numDecimalPlaces;
    private String formatSpec;

    /**
     * No-argument constructor for the class.
     * Default value of 3 given to numDecimalPlaces
     */
    private PrettyFormat() {
        setNumDecimalPlaces(3);
    }

    /**
     * Constructor for the class
     * @param numDecimalPlaces the number of decimal places to round the double to
     */
    PrettyFormat(int numDecimalPlaces) {
        setNumDecimalPlaces(numDecimalPlaces);
    }

    /**
     * Sets the number of decimal places to round up the double to, to the specified value
     * Checks if input is valid before doing so
     * @param numDecimalPlaces the number of decimal places to round up the double to
     */
    private void setNumDecimalPlaces(int numDecimalPlaces) {
        if (numDecimalPlaces < 0) {
            throw new IllegalArgumentException(String.format("Cannot format with < 0 decimal places. (Given: %d)", numDecimalPlaces));
        } else {
            this.numDecimalPlaces = numDecimalPlaces;
            this.formatSpec = "%." + numDecimalPlaces + "f";
        }
    }

    /**
     * Returns the number of decimal places to round up the double to
     * @return the number of decimal places to round up the double to
     */
    public int getNumDecimalPlaces() {
        return numDecimalPlaces;
    }

    /**
     * The core function which provides the formatting for the inputted double
     * @param num the double to format
     * @return the formatted double
     */
    String format(double num) {
        // First round the number according to number of decimal places (earlier specified)
        String numStr = String.format(formatSpec, num);
        int ind = numStr.length() - 1;
        // Checks for trailing zeros
        for (; numStr.charAt(ind) != '.' && numStr.charAt(ind) == '0'; --ind);
        // If trailing zeros go all the way to first digit after decimal point, make it like an integer (in representation)
        // Else just trim the trailing zeros
        String finalStr = numStr.substring(0, numStr.charAt(ind) == '.' ? ind : ind + 1);
        // This check is to ensure that we obey the computerized notation
        // Java follows for doubles < 10^(-3) || >= 10^7
        double roundedNum = Double.parseDouble(finalStr);
        double absRoundedNum = Math.abs(roundedNum);
        // If the rounded double actually does fall under the above criteria
        // use the computerized notation to represent the number instead
        if (absRoundedNum < 0.001 || absRoundedNum >= 10000000.0) {
            return Double.toString(roundedNum);
        // else use the representation we made
        } else {
            return finalStr;
        }
    }

    /**
     * Simple function that when called displays the functionality of the format method
     * Nice way to test the working of the method too
     * @param args null; does not use this at all
     */
    public static void main(String[] args) {
        double num = -0.0055;
        PrettyFormat formatter = new PrettyFormat();
        System.out.printf("The number: %f\nUsing Double.toString(): %s\nUsing String.valueOf(): %s\nFormatted : %s\n", num, Double.toString(num), String.valueOf(num), formatter.format(num));
        num = 1.000;
        System.out.printf("The number: %f\nUsing Double.toString(): %s\nUsing String.valueOf(): %s\nFormatted : %s\n", num, Double.toString(num), String.valueOf(num), formatter.format(num));
        num = 0.399999;
        System.out.printf("The number: %f\nUsing Double.toString(): %s\nUsing String.valueOf(): %s\nFormatted : %s\n", num, Double.toString(num), String.valueOf(num), formatter.format(num));
        num = 15.450;
        System.out.printf("The number: %f\nUsing Double.toString(): %s\nUsing String.valueOf(): %s\nFormatted : %s\n", num, Double.toString(num), String.valueOf(num), formatter.format(num));
        num = Math.pow(10, 40);
        System.out.printf("The number: %f\nUsing Double.toString(): %s\nUsing String.valueOf(): %s\nFormatted : %s\n", num, Double.toString(num), String.valueOf(num), formatter.format(num));
        num = Math.pow(10, 300) + 0.546;
        System.out.printf("The number: %f\nUsing Double.toString(): %s\nUsing String.valueOf(): %s\nFormatted : %s\n", num, Double.toString(num), String.valueOf(num), formatter.format(num));
        formatter.setNumDecimalPlaces(8);
        num = 0.0045672190;
        System.out.printf("The number: %f\nUsing Double.toString(): %s\nUsing String.valueOf(): %s\nFormatted : %s\n", num, Double.toString(num), String.valueOf(num), formatter.format(num));
        formatter.setNumDecimalPlaces(14);
        num = -0.0123456789012345;
        System.out.printf("The number: %f\nUsing Double.toString(): %s\nUsing String.valueOf(): %s\nFormatted : %s\n", num, Double.toString(num), String.valueOf(num), formatter.format(num));
        num = 0.000000014563525621;
        System.out.printf("The number: %f\nUsing Double.toString(): %s\nUsing String.valueOf(): %s\nFormatted : %s\n", num, Double.toString(num), String.valueOf(num), formatter.format(num));
        formatter.setNumDecimalPlaces(0);
        num = 1.2345;
        System.out.printf("The number: %f\nUsing Double.toString(): %s\nUsing String.valueOf(): %s\nFormatted : %s\n", num, Double.toString(num), String.valueOf(num), formatter.format(num));
    }
}
