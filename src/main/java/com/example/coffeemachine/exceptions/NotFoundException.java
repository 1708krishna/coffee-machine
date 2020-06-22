package com.example.coffeemachine.exceptions;

public class NotFoundException extends CoffeeMachineException {

    private static final String EXCEPTION_GENERIC_MESSAGE = "Not found: ";

    public NotFoundException() {
        super(EXCEPTION_GENERIC_MESSAGE);
    }

    public NotFoundException(String message) {
        super(EXCEPTION_GENERIC_MESSAGE + message);
    }
}
