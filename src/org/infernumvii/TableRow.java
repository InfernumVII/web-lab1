package org.infernumvii;

public class TableRow {
    private Cords cords;
    private long currentTimeSeconds;
    private long timeFromStartSeconds;
    private boolean success;
    
    public TableRow(Cords cords, long currentTimeSeconds, long timeFromStartSeconds, boolean success) {
        this.cords = cords;
        this.currentTimeSeconds = currentTimeSeconds;
        this.timeFromStartSeconds = timeFromStartSeconds;
        this.success = success;
    }
    
    public Cords getCords() {
        return cords;
    }

    public long getCurrentTimeSeconds() {
        return currentTimeSeconds;
    }

    public long getTimeFromStartSeconds() {
        return timeFromStartSeconds;
    }
    
    public boolean isSuccess() {
        return success;
    }
}
