package com.techelevator;

import java.text.NumberFormat;

public class Item implements Comparable<Item> {

    private enum ItemType { GUM("Chew"),
        DRINK("Glug"),
        CANDY("Munch"),
        CHIP("Crunch");

        public String getStupidMessage() {
            return stupidMessage;
        }

        private final String stupidMessage;
        ItemType(String verb){
            stupidMessage = String.format("%s %1$s, Yum!", verb);
        }
    }

    public VendingMachine.Slot getMachineSlot() {
        return machineSlot;
    }

    private final VendingMachine.Slot machineSlot;
    private final String name;
    private final int priceInCents;
    private final ItemType type;

    public String getStupidMessage() {
        return type.getStupidMessage();
    }

    public int getQuantityAvailable() {
        return machineSlot.getQuantityAvailable();
    }

    public Item(String machineSlot, String name, String priceInDollars, String strType) {
        this.machineSlot = new VendingMachine.Slot(machineSlot, this);
        this.name = name;
        this.priceInCents = (int)(100*Double.parseDouble(priceInDollars)+0.5);
        type = ItemType.valueOf(strType.toUpperCase());
    }

    public String getName() {
        return name;
    }

    public int getPriceInCents() {
        return priceInCents;
    }

    public String getMessage() {
        return type.getStupidMessage();
    }

    /**
     * Creates an item out of a string in the given Inventory format
     * @param string, pipe delimited Slot|Name|Price|Type
     *
     * @return Item
     */
    public static Item parse(String string) {
        String[] pieces = string.split("\\|");
        return new Item(pieces[0], pieces[1], pieces[2], pieces[3]);
    }

    private final java.text.NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    @Override
    public String toString() {
        String format = "%s\t%-20s%s\t%-8s%s";
        return format.formatted(machineSlot.display(),
                name,
                currencyFormat.format(priceInCents * 0.01),
                type,
                getQuantityAvailable() > 0 ? Integer.toString(getQuantityAvailable()) : "***SOLD OUT***");
    }

    @Override
    public int compareTo(Item o) {
        return this.machineSlot.compareTo(o.machineSlot);
    }
}
