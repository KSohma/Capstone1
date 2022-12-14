package com.techelevator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLog {
    protected String startingAmount;
    protected String newAmount;
    protected DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a ");
    protected String dateTime = LocalDateTime.now().format(formatterDate);
    protected NumberFormat formatterMoney = NumberFormat.getCurrencyInstance();

    public AuditLog() {
    }

    public void log (String choice, int startingAmount, int newAmount){
        this.startingAmount = formatterMoney.format(startingAmount / 100.0);
        this.newAmount = formatterMoney.format(newAmount / 100.0);

        // Updates AuditLog file with provided action
        File log = new File("capstone/src/main/resources/Log.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(log, true))){
            if (choice.equals("MACHINE START")){
                writer.newLine();
            }
            writer.append(dateTime)
                    .append(choice)
                    .append(" ")
                    .append(this.startingAmount)
                    .append(" ")
                    .append(this.newAmount);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
