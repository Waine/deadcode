package com.aurea.deadcode.exception;

/**
 * Created by ekonovalov on 16.03.2017.
 */
public class MalformedExpressionException extends Exception {

    public MalformedExpressionException() {
    }

    public MalformedExpressionException(String message) {
        super(message);
    }

    public MalformedExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedExpressionException(Throwable cause) {
        super(cause);
    }

    public MalformedExpressionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
