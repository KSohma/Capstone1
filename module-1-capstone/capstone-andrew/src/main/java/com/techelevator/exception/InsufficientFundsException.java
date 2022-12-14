package com.techelevator.exception;

import com.techelevator.Item;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Item item, int shortageInCents) {
        super("Deposit %d more cents to buy %s".formatted(shortageInCents, item.getName()));
    }

}
