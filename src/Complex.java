package solver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Complex {
    /*
    Class to create and operate on complex numbers
     */
    private double real;
    private double imag;
    private static final double precision = 0.0001;
    private static PrettyFormat formatter = new PrettyFormat((int) Math.log10(1 / precision));

    /**
     * Constructor for Complex class
     * @param real real part of complex number (double)
     * @param imag imaginary part of complex number (double)
     */
    private Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    /**
     * Converts a String representation of a complex number to
     * an actual complex number. The representation should be
     * of the form [real]+[imag]i with no spaces. i or -i is also acceptable.
     * @param valStr String to parse
     * @return initialised instance of Complex class ('Complex')
     */
    static Complex parseComplex(String valStr) {
        double real = 0.0;
        double imag = 0.0;
        boolean setReal = false;
        boolean setImag = false;

        // Search for the digits:
        // may or may not have the '-' sign
        // but should have at least one digit if it's for the real part
        // not required for imag part because -i and i are accepted
        Pattern compPattern = Pattern.compile("(-*[0-9.]*i|-*[0-9.]+)");
        Matcher compPatternMatcher = compPattern.matcher(valStr);
        try {
            while (compPatternMatcher.find()) {
                // store the match
                String num = compPatternMatcher.group();
                // check for presence of i
                int ind = num.indexOf('i');
                // if only real part present in current match
                if (ind == -1) {
                    // this condition checks to ensure that cases
                    // like "6 i" which otherwise might pass
                    // through the other checks, gets caught here
                    if (compPatternMatcher.end() < valStr.length()) {
                        char check =  valStr.charAt(compPatternMatcher.end());
                        if ((check != '+') && (check != '-')) {
                            throw new NumberFormatException();
                        }
                    }
                    // this condition checks if real part was set before
                    // to avoid invalid cases like "0.9 0.9"
                    if (!setReal) {
                        real = Double.parseDouble(num);
                        setReal = true;
                    } else {
                        throw new NumberFormatException();
                    }
                } else {
                    // this condition checks if real part was set before
                    // to avoid invalid cases like "0.9i i"
                    if (!setImag) {
                        String imagStr = num.substring(0, ind);
                        switch (imagStr) {
                            // we get "" if it's only "i"
                            case "":
                                imag = 1.0;
                                break;
                            // we get this if it's "-i"
                            case "-":
                                imag = -1.0;
                                break;
                            // else we have a legit number before 'i' so try to parseDouble it
                            default:
                                imag = Double.parseDouble(imagStr);
                                break;
                        }
                        setImag = true;
                    } else {
                        throw new NumberFormatException();
                    }
                }
            }
            // if neither of them are set, it means the matcher couldn't
            // match anything which shouldn't be the case so raise an excpetion
            if (!setReal && !setImag) {
                throw new NumberFormatException();
            } else {
                return new Complex(real, imag);
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException(String.format("Cannot convert %s to a complex number!\nMust be represented as [real]+[imag]i with no spaces\ni or -i is also acceptable.", valStr));
        }
    }

    /**
     * Takes the real and imaginary values to create a Complex number object
     * @param realVal the value of the real part of the complex number
     * @param imagVal the value of the imaginary part of the complex number
     * @return initialised instance of Complex class ('Complex')
     */
    static Complex valueOf(double realVal, double imagVal) {
        return new Complex(realVal, imagVal);
    }

    /**
     * Adds two complex numbers.
     * Returns a new Complex instance
     * Follows the principle z1 = a + bi, z2 = c + di
     * z1 + z2 = (a+c) + (b+d)i
     * @param num the complex number to add ('Complex')
     * @return the sum of the complex numbers ('Complex')
     */
    Complex add(Complex num) {
        double real = this.real + num.real;
        double imag = this.imag + num.imag;
        return new Complex(real, imag);
    }

    /**
     * Subtracts two complex numbers.
     * Returns a new Complex instance
     * Follows the principle z1 = a + bi, z2 = c + di
     * z1 - z2 = (a-c) + (b-d)i
     * @param num the complex number to subtract from the calling object ('Complex')
     * @return the difference of the two complex numbers ('Complex')
     */
    Complex subtract(Complex num) {
        double real = this.real - num.real;
        double imag = this.imag - num.imag;
        return new Complex(real, imag);
    }

    /**
     * Multiplies two complex numbers.
     * Returns a new Complex instance
     * Follows the principle z1 = a + bi, z2 = c + di
     * z1 * z2 = (ac - bd) + (ad + bc)i
     * @param num the complex number to multiply ('Complex')
     * @return the product of the two complex numbers ('Complex')
     */
    Complex multiply(Complex num) {
        double real = (this.real * num.real) - (this.imag * num.imag);
        double imag = (this.real * num.imag) + (this.imag * num.real);
        return new Complex(real, imag);
    }

    /**
     * Returns the conjugate of a complex number as a new instance
     * Follows the principle z = a + bi
     * Conj(z) = a - bi
     * @return the conjugate of the complex number ('Complex')
     */
    Complex conjugate() {
        return new Complex(this.real, -1 * this.imag);
    }

    /**
     * Check if the real part of a complex number is equal to a given value
     * Does the checking on the basis of the precision
     * @param real the value to check with the real part (double)
     * @return true if it's equal; false if not
     */
    boolean realPartEquals(double real) {
        return !(Math.abs(this.real - real) >= precision);
    }

    /**
     * Check if the complex part of a complex number is equal to a given value
     * Does the checking on the basis of the precision
     * @param imag the value to check with the imaginary part (double)
     * @return true if it's equal; false if not
     */
    boolean imagPartEquals(double imag) {
        return !(Math.abs(this.imag - imag) >= precision);
    }

    /**
     * Check if two complex numbers are equal or not
     * @param num the complex number to check
     * @return true if they're equal; false if not
     */
    boolean equals(Complex num) {
        return realPartEquals(num.real) && imagPartEquals(num.imag);
    }

    /**
     * Check if a complex number is equal to zero
     * @return true if it's equal; false if not
     */
    boolean isZero() {
        return realPartEquals(0.0) && imagPartEquals(0.0);
    }

    /**
     * Calculates the modulus of a complex number
     * Follows the principle z = a + bi
     * |z| = sqrt(a^2 + b^2)
     * @return the modulus of the complex number (double, not 'Complex')
     */
    double modulus() {
        return Math.sqrt(Math.pow(this.real, 2) + Math.pow(this.imag, 2));
    }

    /**
     * Divides two complex numbers
     * Returns a new Complex instance
     * Follows the principle z1 = a + bi, z2 = c + di
     * z1 / z2 = [z1 * Conj(z2)] / |z2|^2
     * @param num the number to divide the first by
     * @return the result of division of the two complex numbers ('Complex')
     */
    Complex divide(Complex num) {
        if (num.isZero()) {
            throw new ArithmeticException(String.format("Division by zero (%s / %s)!", this.toString(), num.toString()));
        } else {
            Complex numConj = num.conjugate();
            double denominator = Math.pow(num.modulus(), 2);
            Complex divResult = this.multiply(numConj);
            divResult.real /= denominator;
            divResult.imag /= denominator;
            return divResult;
        }
    }

    @Override
    public String toString() {
        if (isZero()) {
            return "0";
        } else if (realPartEquals(0.0)) {
            if (imagPartEquals(1.0)) {
                return "i";
            } else if (imagPartEquals(-1.0)) {
                return "-i";
            } else {
                return String.format("%si", formatter.format(imag));
            }
        } else if (imagPartEquals(0.0)) {
            return formatter.format(real);
        } else {
            String imagStr;
            if (imagPartEquals(1.0)) {
                imagStr = "i";
            } else if (imagPartEquals(-1.0)) {
                imagStr = "-i";
            } else {
                imagStr = String.format("%si", formatter.format(imag));
            }
            return String.format("%s%s%s", formatter.format(real), (imag > precision) ? "+" : "", imagStr);
        }
    }

    /**
     * Simple function that when called displays the functionality of the methods
     * Nice way to test the working of the methods too
     * @param args null; does not use this at all
     */
    public static void main(String[] args) {
        Complex i = Complex.parseComplex("i");
        System.out.println(i);
        Complex negI = Complex.parseComplex("-i");
        System.out.println(negI);
        Complex num1 = Complex.parseComplex("3+2i");
        System.out.printf("num1: %s\n", num1.toString());
        Complex num2 = Complex.parseComplex("1+7i");
        System.out.printf("num2: %s\n", num2.toString());
        System.out.printf("num1 + num2: %s\n", num1.add(num2).toString());
        System.out.printf("num1 - num2: %s\n", num1.subtract(num2).toString());
        num1 = Complex.parseComplex("3+5i");
        System.out.printf("num1: %s\n", num1.toString());
        num2 = Complex.parseComplex("4-3i");
        System.out.printf("num2: %s\n", num2.toString());
        System.out.printf("num1 + num2: %s\n", num1.add(num2).toString());
        System.out.printf("num1 - num2: %s\n", num1.subtract(num2).toString());
        num1 = Complex.parseComplex("3+2i");
        System.out.printf("num1: %s\n", num1.toString());
        num2 = Complex.parseComplex("1+7i");
        System.out.printf("num2: %s\n", num2.toString());
        System.out.printf("num1 * num2: %s\n", num1.multiply(num2).toString());
        num1 = Complex.parseComplex("1+i");
        System.out.printf("num1: %s\n", num1.toString());
        System.out.printf("num1^2: %s\n", num1.multiply(num1).toString());
        num1 = Complex.parseComplex("4-5i");
        System.out.printf("num1: %s\n", num1.toString());
        System.out.printf("num1 * Conj(num1): %s\n", num1.multiply(num1.conjugate()).toString());
        num1 = Complex.parseComplex("2+3i");
        System.out.printf("num1: %s\n", num1.toString());
        num2 = Complex.parseComplex("4-5i");
        System.out.printf("num2: %s\n", num2.toString());
        System.out.printf("num1 / num2: %s\n", num1.divide(num2).toString());
        num1 = Complex.parseComplex("4-5i");
        System.out.printf("num1: %s\n", num1.toString());
        num2 = Complex.valueOf(-1.0, 0.0);
        System.out.printf("num2: %s\n", num2.toString());
        System.out.printf("num2 * num1: %s\n", num2.multiply(num1).toString());
        // Uncomment below lines to see an example of invalid complex number
        // num1 = Complex.parseComplex("6 i");
        // System.out.printf("num1: %s\n", num1.toString());
    }
}
