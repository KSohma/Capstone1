package com.techelevator.log;

import java.io.IOException;

public class AuditLog extends com.techelevator.log.Log {

    public AuditLog() throws IOException {
        super("/log.txt");
    }
}
