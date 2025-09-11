package org.infernumvii;

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
    private final PrintWriter printWriter;

    public FilePrinter(Path path) throws IOException{
        printWriter = new PrintWriter(Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND), true);
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }
    
    public static void main(String[] args) {
        try {
            FilePrinter simpliestLogger = new FilePrinter(Path.of("logs/log.txt"));
            simpliestLogger.getPrintWriter().println("123");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    
}
