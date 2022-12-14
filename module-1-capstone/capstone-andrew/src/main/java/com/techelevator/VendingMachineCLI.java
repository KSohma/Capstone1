package com.techelevator;

import com.techelevator.view.Menu;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.Scanner;

public class VendingMachineCLI {

    private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";

    private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";

    private static final String MAIN_MENU_EXIT = "Exit";

    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_DISPLAY_ITEMS,
            MAIN_MENU_OPTION_PURCHASE,
            MAIN_MENU_EXIT};

    private static final String PURCHASE_MENU_FEED_MONEY = "Feed Money";

    private static final String PURCHASE_MENU_SELECT_PRODUCT = "Select Product";

    private static final String PURCHASE_MENU_FINISH_XACTION = "Finish Transaction";

    private static final String PURCHASE_MENU_AMOUNT_DEPOSITED = "Current amount deposited > ";

    private static final String FEED_MONEY_PROMPT = "Enter an amount to deposit; 0 when done > ";

    private static final String[] PURCHASE_MENU_OPTIONS = {PURCHASE_MENU_FEED_MONEY,
            PURCHASE_MENU_SELECT_PRODUCT, PURCHASE_MENU_FINISH_XACTION};

    private final VendingMachine machine;

    private final Menu mainMenu;

    public VendingMachineCLI(Menu mainMenu) {
        VendingMachine machine1;
        this.mainMenu = mainMenu;
        try {
            machine1 = new VendingMachine();
        } catch (IOException e) {
            machine1 = null;
            System.err.println("Unable to initiate I/O, exiting \n" + e.getMessage());
            System.exit(1);
        }
        machine = machine1;
    }

    public static void main(String[] args) {
        Menu menu = new Menu(System.in, System.out);
        VendingMachineCLI cli = new VendingMachineCLI(menu);
        cli.run();
    }

    private void displayItems() {
        for (var item : machine.getInventory()) {
            System.out.println(item.toString());
        }
    }

    void enterFeedMoneyLoop() {
        int deposit;
        Scanner scanner = new Scanner(System.in);
        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance();
        while (true) {
            System.out.print(FEED_MONEY_PROMPT);
            try {
                deposit = scanner.nextInt();
                if (deposit == 0) return;
                int cents = machine.addCents(100 * deposit);
                System.out.println(PURCHASE_MENU_AMOUNT_DEPOSITED
                        + currencyInstance.format(cents / 100.0)); // note float.0
            } catch (RuntimeException e) {
                System.out.println("Not acceptable money");
            }
        }
    }

    void selectProduct(){
        displayItems();
        System.out.print(" >>> ");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        Scanner scanner = new Scanner(System.in);
        try {
            String str = scanner.next("[A-Za-z]\\d").toUpperCase();
            Item item = machine.makePurchase(str);
            System.out.printf("Dispensed %s price %s balance %s%n%s%n%n",
                    item.getName(),
                    currencyFormat.format(item.getPriceInCents()/100.0),
                    currencyFormat.format(machine.getAvailableCents()/100.0),
                    item.getStupidMessage());
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    void enterPurchaseLoop() {
        Menu purchaseMenu = new Menu(System.in, System.out); // local for future multithread
        while (true) {
            String choice = (String) purchaseMenu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS);
            switch (choice) {
                case PURCHASE_MENU_FEED_MONEY -> enterFeedMoneyLoop();
                case PURCHASE_MENU_SELECT_PRODUCT -> selectProduct();
                case PURCHASE_MENU_FINISH_XACTION -> {
                    var nCoins = machine.finishTransaction();
                    System.out.println("CHANGE");
                    for (int i = 0; i < VendingMachine.coinValues.length; ++i) {
                        System.out.printf("%4d x%4d%n", VendingMachine.coinValues[i], nCoins[i]);
                    }
                    return;
                }
                default -> throw new IllegalStateException("Unexpected value: " + choice);
            }
        }

    }

    private @NotNull File findInventoryFile() {
        return new File(Objects.requireNonNull(getClass().getResource("/vendingmachine.csv")).getFile());
    }

    public void run() {
        try {
            machine.readInventoryFromFile(findInventoryFile());
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        main_loop:
        while (true) {
            String choice = (String) mainMenu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

            switch (choice) {
                case MAIN_MENU_OPTION_DISPLAY_ITEMS:
                    displayItems();
                    break;
                case MAIN_MENU_EXIT:
                    break main_loop;
                case MAIN_MENU_OPTION_PURCHASE:
                    enterPurchaseLoop();
                    break;
                default: // not needed because menu code rejects invalid input
            }
        }
    }
}

