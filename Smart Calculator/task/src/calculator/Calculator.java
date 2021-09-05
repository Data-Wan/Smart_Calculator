package calculator;

import java.math.BigInteger;

public class Calculator {

    private Calculator() {
        throw new IllegalStateException("Utility class");
    }

    public static BigInteger sum(BigInteger number1, BigInteger number2) {
        return number1.add(number2);
    }

    public static BigInteger sub(BigInteger number1, BigInteger number2) {
        return number1.subtract(number2);
    }

    public static BigInteger mult(BigInteger number1, BigInteger number2) {
        return number1.multiply(number2);
    }

    public static BigInteger div(BigInteger number1, BigInteger number2) {
        return number1.divide(number2);
    }

    public static BigInteger pow(BigInteger number1, BigInteger number2) {
        return number1.pow(number2.intValue());
    }
}
