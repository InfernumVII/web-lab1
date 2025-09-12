package com.infernumvii.model;

public class TableRow {
    private Cords cords;
    private String currentTimeSeconds;
    private long timeExecution;
    private boolean success;
    
    public TableRow(Cords cords, String currentTimeSeconds, long timeExecution, boolean success) {
        this.cords = cords;
        this.currentTimeSeconds = currentTimeSeconds;
        this.timeExecution = timeExecution;
        this.success = success;
    }
    
    public Cords getCords() {
        return cords;
    }

    public String getCurrentTimeSeconds() {
        return currentTimeSeconds;
    }
    
    public long getTimeExecution() {
        return timeExecution;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "TableRow [cords=" + cords + ", currentTimeSeconds=" + currentTimeSeconds + ", timeFromStartSeconds="
                + timeExecution + ", success=" + success + "]";
    }  
}
