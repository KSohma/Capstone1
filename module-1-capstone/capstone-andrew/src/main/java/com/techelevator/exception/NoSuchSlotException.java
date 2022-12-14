package com.techelevator.exception;

public class NoSuchSlotException extends RuntimeException {

    public NoSuchSlotException(String slot) {
        super("No such slot = " + slot);
    }
}
