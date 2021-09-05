package calculator;

import calculator.exception.InvalidExpressionsException;
import calculator.exception.UnknownVariableException;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Map<String, BigInteger> variables = new HashMap<>();

        while (true) {

            String input = scanner.nextLine()
                                  .trim();

            if (input.isEmpty()) {
                continue;
            }

            if ("/exit".equals(input)) {
                System.out.println("Bye!");
                return;
            }

            if ("/help".equals(input)) {
                System.out.println("The program calculates the sum, subtraction, multiplication and division of numbers");
                continue;
            }

            if (isUnknownCommand(input)) {
                continue;
            }

            if (tryParsVariableData(variables, input)) {
                continue;
            }

            process(variables, input);
        }
    }

    private static boolean isUnknownCommand(final String input) {
        Pattern unknownCommandPattern = Pattern.compile("[/]\\w*");
        if (unknownCommandPattern.matcher(input)
                                 .matches()) {
            System.out.println("Unknown command");
            return true;
        }
        return false;
    }

    private static boolean tryParsVariableData(final Map<String, BigInteger> variables,
                                               final String input) {
        Pattern variableAssigmentPattern = Pattern.compile(".*\\s*[=].*");

        if (variableAssigmentPattern.matcher(input)
                                    .matches()) {
            final String[] split = input.split("\\s*[=]\\s*");

            if (split.length != 2) {
                System.out.println("Invalid assignment");
                return true;
            }

            if (!isCorrectVarName(split[0])) {
                System.out.println("Invalid identifier");
                return true;
            }

            tryPutData(variables, split);
            return true;
        }
        return false;
    }

    private static void process(final Map<String, BigInteger> variables, final String input) {
        try {
            final BigInteger i = processLine(input, variables);
            System.out.printf("%s%n", i);
        } catch (InvalidExpressionsException | NumberFormatException e) {
            System.out.println("Invalid expression");
        } catch (UnknownVariableException e) {
            System.out.println("Unknown variable");
        }
    }

    private static boolean isCorrectVarName(String varName) {
        Pattern correctVariableNamePattern = Pattern.compile("[a-zA-Z]+");

        return correctVariableNamePattern.matcher(varName)
                                         .matches();
    }

    private static void tryPutData(final Map<String, BigInteger> variables, final String[] split) {
        final BigInteger i;
        try {
            i = new BigInteger(split[1]);
            variables.put(split[0], i);
        } catch (NumberFormatException e) {

            if (!isCorrectVarName(split[1])) {
                System.out.println("Invalid assignment");
                return;
            }

            final BigInteger orDefault = variables.getOrDefault(split[1], null);
            if (orDefault == null) {
                System.out.println("Unknown variable");
                return;
            }
            variables.put(split[0], orDefault);
        }
    }

    public static BigInteger processLine(String line, final Map<String, BigInteger> variables) throws
                                                                                       InvalidExpressionsException,
                                                                                       NumberFormatException,
                                                                                       UnknownVariableException {
        final String postfixNotation = Parser.convertToInfixNormalizedExpression(line);

        final String[] split = postfixNotation.split("\\s+");

        Deque<BigInteger> stackOperands = new ArrayDeque<>();

        for (String next : split) {

            // Check is next symbol is operator
            if (Parser.isOperator(next)) {

                BigInteger secondOperand = stackOperands.pop();
                BigInteger firstOperand = stackOperands.pop();

                if (Parser.isMinus(next)) {
                    stackOperands.push(Calculator.sub(firstOperand, secondOperand));
                } else if (Parser.isPlus(next)) {
                    stackOperands.push(Calculator.sum(firstOperand, secondOperand));
                } else if (Parser.isMultiply(next)) {
                    stackOperands.push(Calculator.mult(firstOperand, secondOperand));
                } else if (Parser.isDivide(next)) {
                    stackOperands.push(Calculator.div(firstOperand, secondOperand));
                } else if (Parser.isPower(next)) {
                    stackOperands.push(Calculator.pow(firstOperand, secondOperand));
                }
            } else {
                BigInteger operand;
                if (isCorrectVarName(next)) {
                    operand = variables.getOrDefault(next, null);
                } else {
                    operand = new BigInteger(next);
                }
                if (operand == null) {
                    throw new UnknownVariableException();
                }
                stackOperands.push(operand);
            }
        }
        if (stackOperands.size() > 1) {
            throw new InvalidExpressionsException();
        }
        return stackOperands.pop();
    }
}
