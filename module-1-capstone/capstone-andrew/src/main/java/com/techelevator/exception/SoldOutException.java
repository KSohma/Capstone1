package com.techelevator.exception;

import com.techelevator.Item;

public class SoldOutException extends RuntimeException {

    public SoldOutException(Item item) {
        super(item.toString());
    }
}
