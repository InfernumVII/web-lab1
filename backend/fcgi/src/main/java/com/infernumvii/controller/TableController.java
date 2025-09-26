package com.infernumvii.controller;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.infernumvii.exception.CordsInvalidFormat;
import com.infernumvii.model.Cords;
import com.infernumvii.model.TableRow;

public class TableController {
    private final Connection connection;
    private final Gson gson = new Gson();
    private static final int HISTORY_SIZE = 18;
    private final String userId;

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

    public TableController(Connection connection, String userId) {
        this.connection = connection;
        this.userId = userId;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS results (
                id SERIAL PRIMARY KEY,
                user_id VARCHAR(32) NOT NULL,
                x VARCHAR(50) NOT NULL,
                y TEXT NOT NULL,
                r VARCHAR(50) NOT NULL,
                crrent_time VARCHAR(50) NOT NULL,
                execution_time BIGINT NOT NULL,
                success BOOLEAN NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table", e);
        }
    }

    private void storeRow(TableRow row) throws SQLException {
        String sql = "INSERT INTO results (user_id, x, y, r, crrent_time, execution_time, success) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, Integer.toString(row.getCords().getX()));
            pstmt.setString(3, row.getCords().getY().toPlainString());
            pstmt.setString(4, Integer.toString(row.getCords().getR()));
            pstmt.setString(5, row.getCurrentTimeSeconds());
            pstmt.setLong(6, row.getTimeExecution());
            pstmt.setBoolean(7, row.isSuccess());
            pstmt.executeUpdate();
        }
        
        
        cleanupOldRecords(userId);
    }

    private void cleanupOldRecords(String userId) throws SQLException {
        String cleanupSql = """
            DELETE FROM results 
            WHERE id NOT IN (
                SELECT id FROM results 
                WHERE user_id = ? 
                ORDER BY created_at DESC 
                LIMIT ?
            ) AND user_id = ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(cleanupSql)) {
            pstmt.setString(1, userId);
            pstmt.setInt(2, HISTORY_SIZE);
            pstmt.setString(3, userId);
            pstmt.executeUpdate();
        }
    }

    private TableRow parseRow(String rawJson, long startTime) throws CordsInvalidFormat {
        Cords cords = gson.fromJson(rawJson, Cords.class);
        cords.validateCords();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        boolean success = cords.IsPointInTheArea();
        return new TableRow(
            cords,
            formatter.format(date),
            TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTime), 
            success
        );
    }

    public String storeRowAndReturnAllTable(String rawJson, long startTime) throws CordsInvalidFormat, SQLException {
        TableRow row = parseRow(rawJson, startTime);
        storeRow(row);
        
        return returnAllTable(startTime);
    }

    public String returnAllTable(long startTime) throws SQLException {
        List<TableRow> history = getHistory(userId);
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

    private List<TableRow> getHistory(String userId) throws SQLException {
        List<TableRow> history = new ArrayList<>();
        String sql = "SELECT x, y, r, crrent_time, execution_time, success FROM results WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setInt(2, HISTORY_SIZE);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String x = rs.getString("x");
                BigDecimal y = new BigDecimal(rs.getString("y"));
                String r = rs.getString("r");
                String currentTime = rs.getString("crrent_time");
                long executionTime = rs.getLong("execution_time");
                boolean success = rs.getBoolean("success");
                
                Cords cords = new Cords(Integer.parseInt(x), y, Integer.parseInt(r));
                history.add(new TableRow(cords, currentTime, executionTime, success));
            }
        }
        return history;
    }
}