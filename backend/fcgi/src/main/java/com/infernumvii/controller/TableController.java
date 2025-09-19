package com.infernumvii.controller;

import java.text.SimpleDateFormat;
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

    private static final String tableHeader = """
    <tr>
        <th>x</th>
        <th>y</th>
        <th>R</th>
        <th>currentTime</th>
        <th>timeExecution</th>
        <th>Success</th>
    </tr>
    """;

    public TableController(){
    }

    private void storeRow(TableRow row){
        if (history.size() >= HISTORY_SIZE) {
            history.removeFirst(); 
        }
        history.addLast(row);
    }

    private TableRow parseRow(String rawJson, long startTime) throws CordsInvalidFormat{
        Cords cords = gson.fromJson(rawJson, Cords.class);
        cords.validateCords();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        boolean success = cords.IsPointInTheArea();
        TableRow tableRow = new TableRow(
            cords,
            formatter.format(date),
            TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTime), 
            success
        );
        
        return tableRow;
    }

    public String storeRowAndReturnAllTable(String rawJson, long startTime) throws CordsInvalidFormat{
        storeRow(parseRow(rawJson, startTime));
        String tableContent = tableHeader;
        for (TableRow tableRow : history) {
            tableContent += String.format("""
            <tr>
                <td>%s</td>
                <td>%s</td>
                <td>%s</td>
                <td>%s</td>
                <td>%s micros</td>
                <td>%s</td>
            </tr>
            """, 
            tableRow.getCords().getX(),
            tableRow.getCords().getY().toPlainString(),
            tableRow.getCords().getR(),
            tableRow.getCurrentTimeSeconds(),
            tableRow.getTimeExecution(),
            tableRow.isSuccess()
            );
        }
        return tableContent;
    }
    
    
}
