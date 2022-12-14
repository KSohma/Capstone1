package com.techelevator;

import com.techelevator.exception.InsufficientFundsException;
import com.techelevator.exception.NoSuchSlotException;
import com.techelevator.exception.SoldOutException;
import com.techelevator.log.AuditLog;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

public class VendingMachine {

    public static class Slot implements Comparable<Slot> {

        private static final Map<String, Slot> slotMap = new HashMap<>();

        private final String strSlot;

        private int quantityAvailable = INITIAL_QUANTITY;

        private Item item;

        Slot(String str, Item item) {
            strSlot = str;
            this.item = item;
            slotMap.put(str, this);
        }

        public int getQuantityAvailable() {
            return quantityAvailable;
        }

        public void decrementQuantity() {
            --quantityAvailable;
        }

        public final String display() {
            return strSlot;
        }

        @Override
        public int compareTo(@NotNull VendingMachine.Slot o) {
            return strSlot.compareTo(o.strSlot);
        }
    }

    private abstract class LogEntry {

        final int initialBalance = VendingMachine.this.availableCents;

        private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        protected abstract String getMessage();

        public String toString() {
            return "%s %s %s".formatted(getMessage(),
                    currencyFormat.format(initialBalance * 0.01),
                    currencyFormat.format(VendingMachine.this.availableCents * 0.01));
        }

        protected final void write() {
            VendingMachine.this.log.log(this.toString());
        }
    }

    static final int[] coinValues = {25, 10, 5};

    private static final int INITIAL_QUANTITY = 5;

    private final AuditLog log = new AuditLog();

    private final TreeMap<Slot, Item> inventory = new TreeMap<>(); // want sorted!

    private int availableCents;

    public VendingMachine() throws IOException {
    }

    public int getAvailableCents() {
        return availableCents;
    }

    public Collection<Item> getInventory() {
        return inventory.values();
    }

    public Item makePurchase(String strSlot) {
        class LogPurchase extends LogEntry {

            private final String message;

            private LogPurchase(Item item) {
                super();
                message = "%s %s".formatted(item.getName(), item.getMachineSlot().display());
            }

            @Override
            protected String getMessage() {
                return message;
            }
        }

        Slot slot = Slot.slotMap.get(strSlot);
        if (slot == null) throw new NoSuchSlotException(strSlot);
        var purchasedItem = returnItemIfAvailable(slot); // throws on fail
        var logEntry = new LogPurchase(purchasedItem);
        slot.decrementQuantity();
        availableCents -= purchasedItem.getPriceInCents();
        logEntry.write();
        return purchasedItem;
    }

    public int[] finishTransaction() {
        class LogFinish extends LogEntry {

            @Override
            protected String getMessage() {
                return "MAKE CHANGE";
            }
        }
        var logEntry = new LogFinish();
        // make change
        int[] nCoins = new int[coinValues.length];
        for (int i = 0; availableCents > 0 && i < coinValues.length; ++i) {
            final var nCurrentCoin = availableCents / coinValues[i];
            nCoins[i] = nCurrentCoin;
            availableCents -= coinValues[i] * nCurrentCoin;
        }
        logEntry.write();
        return nCoins;
    }


    Item returnItemIfAvailable(Slot slot) {
        Item item = inventory.get(slot);
        assert item != null; // anything that is a valid slot has is in inventory
        if (item.getQuantityAvailable() == 0)
            throw new SoldOutException(item);
        if (item.getPriceInCents() > availableCents)
            throw new InsufficientFundsException(item, item.getPriceInCents() - availableCents);
        return item;
    }

    int addCents(int cents) {

        class LogFeed extends LogEntry {

            @Override
            protected String getMessage() {
                return "FEED MONEY";
            }
        }
        LogEntry logEntry = new LogFeed();
        try {
            return (availableCents += cents);
        } finally {
            logEntry.write();
        }
    }

    void readInventoryFromFile(File inventoryFile) throws FileNotFoundException {
        Scanner scanner = new Scanner(inventoryFile);
        while (scanner.hasNextLine()) {
            Item item = Item.parse(scanner.nextLine());
            inventory.put(item.getMachineSlot(), item);
        }
    }
}
