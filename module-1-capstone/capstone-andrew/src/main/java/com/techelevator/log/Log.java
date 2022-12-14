package com.techelevator.log;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    File logFile;
    PrintWriter printWriter;

    public Log(String fileName) throws IOException {
        var url = getClass().getResource(fileName);
        logFile = url != null
                ? new File(url.getFile())
                : new File(getClass().getResource("/").getPath(), fileName);
        printWriter = new PrintWriter(new FileWriter(logFile, true), true);
    }
    public void log(String s) {
        try {
            printWriter.println(dateTimeFormatter.format(LocalDateTime.now()) + "\t" + s);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
