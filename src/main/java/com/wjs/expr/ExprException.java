package com.wjs.expr;

/**
 * @author wjs
 * @date 2019-12-31 22:33
 **/
public class ExprException extends RuntimeException {

    public ExprException() {
    }

    public ExprException(String message) {
        super(message);
    }

    public ExprException(Throwable cause) {
        super(cause);
    }

    public ExprException(String message, Throwable cause) {
        super(message, cause);
    }
}
