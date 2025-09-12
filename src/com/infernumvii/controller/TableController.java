package com.infernumvii.controller;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.infernumvii.model.Cords;
import com.infernumvii.model.TableRow;

public class TableController {
    private final int HISTORY_SIZE = 18;
    private final Deque<TableRow> history = new ArrayDeque<TableRow>(HISTORY_SIZE);
    private long startTimeSeconds = 0;
    private Gson gson = new Gson();

    public TableController(){
        startTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    private void storeRow(TableRow row){
        if (history.size() >= HISTORY_SIZE) {
            history.removeFirst(); 
        }
        history.addLast(row);
    }

    private TableRow parseRow(String rawJson){
        Cords cords = gson.fromJson(rawJson, Cords.class);
        boolean success = cords.IsPointInTheArea();
        TableRow tableRow = new TableRow(
            cords,
            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()),
            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - startTimeSeconds,
            success
        );
        return tableRow;
    }

    public String storeRowAndReturnAllTable(String rawJson){
        storeRow(parseRow(rawJson));
        return gson.toJson(history);
    }   
}
