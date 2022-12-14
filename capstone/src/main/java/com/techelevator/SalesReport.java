package com.techelevator;

import java.io.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class SalesReport {
    private final VendingItems vendingItems = new VendingItems();
    private final TreeMap<String, Integer> salesReport = new TreeMap<>();
    private final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy.MM.dd_kk.mm.ss");
    private final String dateTime = LocalDateTime.now().format(formatterDate);
    private final NumberFormat formatterMoney = NumberFormat.getCurrencyInstance();
    private Object[] tempItem = new Object[5];
    private File file;
    private int totalSales;

    public SalesReport(){
        // creates item list for SalesReport and sets initial sale number
        for (Map.Entry<String, Object[]> item: vendingItems.itemStock.entrySet()){
            tempItem = item.getValue();
            salesReport.put((String) tempItem[1], 0);
        }
    }

    public void addSalesReport (String item){
        // updates item list when an item is sold
        item = item.toUpperCase(Locale.ROOT);
        if (vendingItems.itemStock.containsKey(item)) {
            tempItem = vendingItems.itemStock.get(item);
            String itemName = (String) tempItem[1];

            if (salesReport.containsKey(itemName)) {
                int sold = salesReport.get(itemName) + 1;
                salesReport.put(itemName, sold);

                int itemCost = Integer.parseInt((String)tempItem[2]);
                totalSales += itemCost;
            }
        }
    }

    public void createReportFile () throws IOException {
        // creates SalesReport file with current date/time
        String newFile = "capstone/src/main/resources/SalesReport_" + dateTime + ".txt";
        file = new File(newFile);
        file.createNewFile();
    }

    public void writeReportFile () throws IOException {
        // writes the contents of item list to SalesReport file
        if (file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.append("Sales Report (Item|Sales)");
                writer.newLine();
                for (Map.Entry<String, Integer> sale : salesReport.entrySet()) {
                    writer.append(sale.getKey()).append("|").append(String.valueOf(sale.getValue()));
                    writer.newLine();
                }
                writer.newLine();
                double printedSales = (totalSales / 100.0);
                writer.append("Total Sales : ").append(formatterMoney.format(printedSales));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IOException("File does not exist");
        }
    }

    public void printSalesReport(){
        // prints item list from secret menu option
        DateTimeFormatter formatterPrintedReport = DateTimeFormatter.ofPattern("MM.dd.yyy - hh.mm.ss a");
        System.out.println("\nSales Report (" + LocalDateTime.now().format(formatterPrintedReport) + ")");
        for (Map.Entry<String, Integer> sale : salesReport.entrySet()){
            System.out.println(sale.getKey() + "|" + sale.getValue());
        }
        System.out.println("\nTotal Sales : " + formatterMoney.format((totalSales / 100.0)));
    }

}
