package com.example.coffeemachine.exceptions;

public class NotAllowedException extends CoffeeMachineException {

    private static final String EXCEPTION_GENERIC_MESSAGE = "Not allowed: ";

    public NotAllowedException() {
        super(EXCEPTION_GENERIC_MESSAGE);
    }

    public NotAllowedException(String message) {
        super(EXCEPTION_GENERIC_MESSAGE + message);
    }
}
