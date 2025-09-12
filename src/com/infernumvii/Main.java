package com.infernumvii;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fastcgi.*;
import com.google.gson.*;
import com.infernumvii.controller.FCGIController;
import com.infernumvii.controller.TableController;
import com.infernumvii.util.FilePrinter;

public class Main {
    private static FilePrinter filePrinter = new FilePrinter(Path.of("logs/log.txt"));
    public static void main(String[] args) {
        FCGIController fcgiController = new FCGIController("9000");
        TableController tableController = new TableController();
        try {
            fcgiController.start(tableController::storeRowAndReturnAllTable);
        } catch (Exception e) {
            filePrinter.getPrintWriter().println(String.format("Error: %s", e.getMessage()));
        }
    }
    public static FilePrinter getFilePrinter() {
        return filePrinter;
    }
}
