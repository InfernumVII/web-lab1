package com.infernumvii.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class FilePrinter {
    private PrintWriter printWriter = null;

    public FilePrinter(Path path){
        try {
            printWriter = new PrintWriter(Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }
    
    public static void main(String[] args) {
        FilePrinter simpliestLogger = new FilePrinter(Path.of("logs/log.txt"));
        simpliestLogger.getPrintWriter().println("123");
    }


    
}
