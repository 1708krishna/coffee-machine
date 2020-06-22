package com.example.coffeemachine.exceptions;

public abstract class CoffeeMachineException extends RuntimeException {

    public CoffeeMachineException() {
        super();
    }

    public CoffeeMachineException(String message) {
        super(message);
    }

    public CoffeeMachineException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoffeeMachineException(Throwable cause) {
        super(cause);
    }

    protected CoffeeMachineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
