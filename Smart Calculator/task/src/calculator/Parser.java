package calculator;

import calculator.exception.InvalidExpressionsException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.regex.Pattern;

public class Parser {

    private static final String space = " ";

    private Parser() {
        throw new IllegalStateException("Parser - Utils class");
    }

//    public static void main(String[] args) throws InvalidExpressionsException {
//        String str = "-15  * -10";
//
//        String normalizedExpression = getNormalizedExpression(str);
//
//        System.out.println(normalizedExpression);
//        final String x = infixToPostfixNotation(normalizedExpression);
//        System.out.println(x);
//        System.out.println(normalizeExpression(x));
//    }

    public static String convertToInfixNormalizedExpression(String str) throws InvalidExpressionsException {
        return  infixToPostfixNotation(getNormalizedExpression(str).trim()).trim();
    }
    private static String getNormalizedExpression(String expression) throws InvalidExpressionsException {
        do {
            expression = normalizeExpression(expression);
        } while (!normalizeExpression(expression).equals(expression));

        return expression;
    }

    private static String infixToPostfixNotation(String line) throws InvalidExpressionsException {

        line = line.replaceAll("\\s+", "");
        StringBuilder result = new StringBuilder();

        Deque<Character> stack = new ArrayDeque<>();

        for (int i = 0; i < line.length(); i++) {
            char currentChar = line.charAt(i);

            if (Prec(currentChar) > 0) {
                while (!stack.isEmpty() && Prec(stack.peek()) >= Prec(currentChar)) {
                    addOperator(stack.pop(), result);
                }
                stack.push(currentChar);
            } else if (currentChar == ')') {
                char x = stack.pop();
                while (x != '(') {
                    if (stack.isEmpty()) {
                        throw new InvalidExpressionsException();
                    }
                    addOperator(x, result);
                    x = stack.pop();
                }
            } else if (currentChar == '(') {
                stack.push(currentChar);
            }

            //character is neither operator nor (
            else {
                i  = addOperand(line, result, i);
            }
        }
        // 7 At the end of the expression, pop the stack and add all operators to the result.
        while (!stack.isEmpty()) {
            addOperator(stack.pop(), result);
        }
        return result.toString();
    }

    private static int addOperand(final String line, final StringBuilder result, final int i) {
        int j = i;
        for (; j < line.length() && Character.isLetterOrDigit(line.charAt(j)); j++) {

            char currentChar1 = line.charAt(j);

            result.append(currentChar1);
        }
        result.append(space);
        return j - 1;
    }

    private static String normalizeExpression(String expression) throws InvalidExpressionsException {
        StringBuilder result = new StringBuilder();

        Deque<String> deque = new ArrayDeque<>();
        for (String next : expression.split("")) {
            if (next.isBlank()) {
                continue;
            }

            if (Prec(next.charAt(0)) > 0) {
                if (Objects.equals(deque.peek(), next)) {
                    if (isMinus(next)) {
                        deque.pop();
                        deque.push("+");
                    } else if (!isPlus(next)) {
                        throw new InvalidExpressionsException(next);
                    }
                } else {
                    deque.add(next);
                }
            } else {
                processOperators(result, deque);
                result.append(next);
            }
        }

        if (!deque.isEmpty()) {
            throw new InvalidExpressionsException("Operators after end of expression");
        }

        return result.toString();
    }

    /**
     * A utility function to return
     * precedence of a given operator
     * Higher returned value means
     * higher precedence.
     *
     * @param ch the ch
     * @return the int
     */
    static int Prec(char ch) {
        switch (ch) {
            case '+':
            case '-':
                return 1;

            case '*':
            case '/':
                return 2;

            case '^':
                return 3;
        }
        return -1;
    }

    private static void addOperator(Character character, StringBuilder sb) throws InvalidExpressionsException {
        if (!isOperator(character.toString())) {
            throw new InvalidExpressionsException(String.format("Used as operator %s", character));
        }
        sb.append(character);
        sb.append(space);
    }

    public static boolean isMinus(final String s) {
        Pattern isOperators = Pattern.compile("[-]+");

        return isOperators.matcher(s)
                          .matches();
    }

    public static boolean isPlus(final String s) {
        Pattern isOperators = Pattern.compile("[+]+");

        return isOperators.matcher(s)
                          .matches();
    }

    private static void processOperators(final StringBuilder result, final Deque<String> deque) {
        while (!deque.isEmpty()) {
            var currentOperand = deque.pop();
            if (isPlus(currentOperand) && !deque.isEmpty()) {
                final String nextOperand = deque.peek();
                if (isMinus(nextOperand)) {
                    deque.pop();
                    deque.push("-");
                } else {
                    addOperatorToExpression(result, currentOperand);
                }
            } else if (isMinus(currentOperand) && !deque.isEmpty()) {
                final String nextOperand = deque.peek();
                if (isPlus(nextOperand)) {
                    deque.pop();
                    deque.push("-");
                } else {
                    addOperatorToExpression(result, currentOperand);
                }
            } else {
                addOperatorToExpression(result, currentOperand);
            }
        }
    }

    public static boolean isOperator(final String s) {
        Pattern isOperators = getPatternForOperators();

        return isOperators.matcher(s)
                          .matches();
    }

    private static void addOperatorToExpression(final StringBuilder result, final String next) {
        result.append(space);
        result.append(next);
        result.append(space);
    }

    public static Pattern getPatternForOperators() {
        return Pattern.compile("[+\\-^/*]");
    }



    public static boolean isDivide(final String s) {
        Pattern isOperators = Pattern.compile("[/]");

        return isOperators.matcher(s)
                          .matches();
    }

    public static boolean isMultiply(final String s) {
        Pattern isOperators = Pattern.compile("[*]");

        return isOperators.matcher(s)
                          .matches();
    }

    public static boolean isPower(final String s) {
        Pattern isOperators = Pattern.compile("[\\^]");

        return isOperators.matcher(s)
                          .matches();
    }
}
