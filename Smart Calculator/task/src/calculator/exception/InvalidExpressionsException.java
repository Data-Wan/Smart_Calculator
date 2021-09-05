package calculator.exception;

public class InvalidExpressionsException extends Exception {
    public InvalidExpressionsException(final String s) {
        super(s);
    }

    public InvalidExpressionsException() {
        super();
    }
}
