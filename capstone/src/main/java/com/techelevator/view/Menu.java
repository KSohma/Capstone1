package com.techelevator.view;

import com.techelevator.AuditLog;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Scanner;



public class Menu {

	private PrintWriter out;
	private Scanner in;
	protected static int balance;
	protected static final NumberFormat formatterMoney = NumberFormat.getCurrencyInstance();

	public Menu() {
	}

	public Menu(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.parseInt(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) {
			out.println(System.lineSeparator() + "*** " + userInput + " is not a valid option ***" + System.lineSeparator());
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			if (!options[i].equals("")){
				out.println(optionNum + ") " + options[i]);
			}

		}
		out.println(System.lineSeparator() + "Current Money Provided: " + formatterMoney.format(balance / 100.0));
		out.print(System.lineSeparator() + "Please choose an option >>> ");
		out.flush();
	}



	public void updateBalance (String choice, String dollarAmount, String log){
		AuditLog auditLog = new AuditLog();
		int initialBalance = balance;
		int updatedBalance = balance;
		int intDollarAmount = Integer.parseInt(dollarAmount);

		if (choice.equals("Add")){
			updatedBalance += intDollarAmount;
		}
		if (choice.equals("Subtract")){
			updatedBalance -= intDollarAmount;
		}
		balance = updatedBalance;
		auditLog.log(log, initialBalance, balance);
		setBalance(balance);
	}


	public void change (){
		int numOfQuarters = 0;
		int dimes = 0;
		int nickels = 0;
		int initialBalance = balance;
		AuditLog auditLog = new AuditLog();

		while (balance != 0) {
			if (balance >= 25) {
				numOfQuarters =  balance / 25;
				balance -= numOfQuarters * 25;
			} else if (balance >= 10) {
				balance -= 10;
				dimes++;
			} else if (balance >= 5) {
				balance -= 5;
				nickels++;
			}
		}
		System.out.println("Your change is " + formatterMoney.format(initialBalance / 100.0)
				+ " in " + numOfQuarters
				+ " quarters, " + dimes
				+ " dimes, and " + nickels
				+ " nickels.");
		auditLog.log("GIVE CHANGE", initialBalance, balance);
	}

	public void setBalance(int balance) {
		Menu.balance = balance;
	}
	public int getBalance() {
		return balance;
	}

}
