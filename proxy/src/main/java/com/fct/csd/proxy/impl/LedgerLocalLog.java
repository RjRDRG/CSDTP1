package com.fct.csd.proxy.impl;

import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

@Component
public class LedgerLocalLog implements AutoCloseable{

    FileWriter myWriter;

    public LedgerLocalLog() {
        try {
            myWriter = new FileWriter("ledgerLocalLog.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String s) throws IOException {
      myWriter.write(s + "\n");
      myWriter.flush();
    }

    @Override
    public void close() throws Exception {
        myWriter.close();
    }
}
