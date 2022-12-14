package com.techelevator;

import com.techelevator.view.Menu;
import java.io.IOException;
import java.util.Scanner;


public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String MAIN_MENU_OPTION_SALES_REPORT = "";

	private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_DISPLAY_ITEMS,
														MAIN_MENU_OPTION_PURCHASE,
														MAIN_MENU_OPTION_EXIT,
														MAIN_MENU_OPTION_SALES_REPORT};

	private static final String PURCHASE_MENU_OPTION_FEED_MONEY = "Feed Money";
	private static final String PURCHASE_MENU_OPTION_SELECT_PRODUCT = "Select Product";
	private static final String PURCHASE_MENU_OPTION_FINISH_TRANSACTION = "Finish Transaction";

	private static final String[] PURCHASE_MENU_OPTION = {PURCHASE_MENU_OPTION_FEED_MONEY,
															PURCHASE_MENU_OPTION_SELECT_PRODUCT,
															PURCHASE_MENU_OPTION_FINISH_TRANSACTION};

	private static final String[] FEED_MONEY_MENU_OPTION = {"$1",
															"$2",
															"$5",
															"$10",
															"Return to Purchase Menu"};

	// pulls item list from Vending Items
	private final VendingItems vendingItems = new VendingItems();
	private final Menu menu;
	private final AuditLog auditLog = new AuditLog();
	private final Scanner scanner = new Scanner(System.in);
	private final SalesReport salesReport = new SalesReport();

	public VendingMachineCLI(Menu menu){
		this.menu = menu;
	}

	// Vending Machine application
	public void run() throws IOException {
		String choiceString;
		auditLog.log("MACHINE START", 0, 0);
		salesReport.createReportFile();
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				// display vending machine items
				vendingItems.displayItems(1);
			}
			if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				// purchase submenu
				boolean purchaseMenu = true;
				do {
					String purchaseChoice = (String) menu.getChoiceFromOptions(PURCHASE_MENU_OPTION);
					if (purchaseChoice.equals(PURCHASE_MENU_OPTION_FEED_MONEY)) {
						// adds money to total in whole bill amounts
						boolean feedMenu = true;
						do {
							choiceString = "FEED MONEY";
							System.out.println("Enter money is whole bill amounts ($1, $2, $5, $10)");
							String money = (String) menu.getChoiceFromOptions(FEED_MONEY_MENU_OPTION);
							if (money.equals("$1")) {
								menu.updateBalance("Add", "100", choiceString);
							} else if (money.equals("$2")) {
								menu.updateBalance("Add", "200", choiceString);
							} else if (money.equals("$5")) {
								menu.updateBalance("Add", "500", choiceString);
							} else if (money.equals("$10")) {
								menu.updateBalance("Add", "1000", choiceString);
							} else if (money.equals("Return to Purchase Menu")) {
								feedMenu = false;
							}
						} while (feedMenu);
					}
					if (purchaseChoice.equals(PURCHASE_MENU_OPTION_SELECT_PRODUCT)) {
						// removes selected products from machine stock and subtracts cost from balance
						boolean selectProduct = true;
						String selection;
						do {
							vendingItems.displayItems(2);
							System.out.print("Please input the slot number for your desired item >>> ");
							selection = scanner.nextLine();
							if (selection.equals("1")){
								selectProduct = false;
							} else {
								System.out.println(vendingItems.selectProducts(selection));
								salesReport.addSalesReport(selection);
							}
						} while (selectProduct);
					}
					if (purchaseChoice.equals(PURCHASE_MENU_OPTION_FINISH_TRANSACTION)) {
						// returns remaining money as change and exits Purchase menu
						menu.change();
						purchaseMenu = false;
					}
				} while (purchaseMenu);
			}
			if (choice.equals(MAIN_MENU_OPTION_EXIT)) {
				// exits system
				System.out.println("\nThank you for shopping with Umbrella Corp!");
				auditLog.log("MACHINE CLOSING", 0, 0);
				salesReport.writeReportFile();
				System.exit(0);
			}
		if (choice.equals(MAIN_MENU_OPTION_SALES_REPORT)) {
				// prints sales report
				salesReport.printSalesReport();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}

}
