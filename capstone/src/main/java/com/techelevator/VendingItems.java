package com.techelevator;

import com.techelevator.view.Menu;

import java.io.File;
import java.io.IOException;
import java.util.*;
//handles vending machine
public class VendingItems extends Menu {
    private final Menu menu = new Menu();
    protected final Map<String, Object[]> itemStock = new TreeMap<>();

    public VendingItems(){
        // populates item list from vending machine file
        File file = new File("capstone/vendingmachine.csv");
        try (Scanner setup = new Scanner(file)){
            while (setup.hasNextLine()) {
                String[] itemList = setup.nextLine().split("\\|");
                int stock = 5;
                int tempPrice = (int) (Double.parseDouble(itemList[2]) * 100.0);
                itemList[2] = Integer.toString(tempPrice);
                Object[] itemListWithStock = {itemList[0], itemList[1], (itemList[2]), itemList[3], stock};
                itemStock.put(itemList[0], itemListWithStock);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayItems (int menuOption){
        // prints item list with stock for vending machine menu option
        int totalStrLength = 18;
        String stringItem = "Item";
        System.out.println("***************************************");
        System.out.printf("%s %-18s %9s %6s",
                "Slot",
                String.format("%-18s",
                        String.format("%" + (stringItem.length() + (totalStrLength - stringItem.length())/2 + "s")
                                , stringItem)),
                "Price",
                "Stock");

        for (Map.Entry<String, Object[]> item : itemStock.entrySet()) {
            Object[] itemArray = item.getValue();
            String itemName = itemArray[1].toString();
            System.out.printf("%s %-" + totalStrLength + "s %s %s",
                    "\n" +  itemArray[0]
                            + " - ", String.format("%-" + totalStrLength + "s",
                                     String.format("%" + (itemName.length() + (totalStrLength - itemName.length())/2 + "s")
                                             , itemName))
                            + " - ", formatterMoney.format(Double.parseDouble((String)itemArray[2]) / 100.0)
                            + " - ", itemArray[4]);

        }
        System.out.println();
        System.out.println("***************************************");
        if (menuOption == 2){

            System.out.println("Current Money Provided: " + formatterMoney.format( balance / 100.0));
            System.out.println("\n1) Return to Previous Menu\n");
        }
    }

    public String selectProducts (String selection){
        // pulls given item for sales and updates inventory and customer balance
        String returnStatement = "";
        for (Map.Entry<String, Object[]> item: itemStock.entrySet()){
            if (item.getKey().equalsIgnoreCase(selection)) {
                Object[] selectedItem = item.getValue();
                if (balance >= Integer.parseInt((String) selectedItem[2])) {
                     if (!((int)selectedItem[4] == 0)) {
                        String auditString = selectedItem[1] + " " + selectedItem[0];
                        menu.updateBalance("Subtract", (String) selectedItem[2], auditString);
                        int productCount = ((Integer) selectedItem[4]) - 1;
                        selectedItem[4] = productCount;
                        if (selectedItem[3].equals("Chip")) {
                            returnStatement = "\nCrunch Crunch, Yum!\n";
                        }
                        if (selectedItem[3].equals("Candy")) {
                            returnStatement = "\nMunch Munch, Yum!\n";
                        }
                        if (selectedItem[3].equals("Drink")) {
                            returnStatement = "\nGlug Glug, Yum!\n";
                        }
                        if (selectedItem[3].equals("Gum")) {
                            returnStatement = "\nChew Chew, Yum!\n";
                        }
                    } else returnStatement = "\n**Item out of stock**\n";
                } else returnStatement = "\n**Insufficient funds**\n";
                item.setValue(selectedItem);
                break;
            } else {
                returnStatement = "\n**Invalid selection**\n";
            }
        } return returnStatement;
    }
}
