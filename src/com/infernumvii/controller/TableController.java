package com.infernumvii.controller;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.infernumvii.exception.CordsInvalidFormat;
import com.infernumvii.model.Cords;
import com.infernumvii.model.TableRow;

public class TableController {
    private final int HISTORY_SIZE = 18;
    private final Deque<TableRow> history = new ArrayDeque<TableRow>(HISTORY_SIZE);
    private final Gson gson = new Gson();

    public TableController(){
    }

    private void storeRow(TableRow row){
        if (history.size() >= HISTORY_SIZE) {
            history.removeFirst(); 
        }
        history.addLast(row);
    }

    private TableRow parseRow(String rawJson) throws CordsInvalidFormat{
        long startTimeSeconds = System.currentTimeMillis();
        Cords cords = gson.fromJson(rawJson, Cords.class);
        cords.validateCords();
        boolean success = cords.IsPointInTheArea();
        TableRow tableRow = new TableRow(
            cords,
            new Date(System.currentTimeMillis()).toString(),
            System.currentTimeMillis() - startTimeSeconds,
            success
        );
        
        return tableRow;
    }

    public String storeRowAndReturnAllTable(String rawJson) throws CordsInvalidFormat{
        storeRow(parseRow(rawJson));
        return gson.toJson(history);
    }   
}
