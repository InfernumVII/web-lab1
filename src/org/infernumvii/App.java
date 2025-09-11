package org.infernumvii;
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
import java.util.stream.Collectors;

import com.fastcgi.*;
import com.google.gson.*;

public class App {
    private final static int HISTORY_SIZE = 18;
    private static Deque<Cords> history = new ArrayDeque<Cords>(HISTORY_SIZE);
    public static void main(String[] args) throws IOException {
        System.setProperty("FCGI_PORT", "9000");
        FilePrinter filePrinter = new FilePrinter(Path.of("logs/log.txt"));
        var fcgiInterface = new FCGIInterface();
        while (fcgiInterface.FCGIaccept() >= 0) {
            var method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
    
            if (method.equals("POST")) {
                int contentLength = Integer.parseInt(FCGIInterface.request.params.getProperty("CONTENT_LENGTH"));
                byte[] bytes = FCGIInterface.request.inStream.readNBytes(contentLength);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(byteArrayInputStream));
                String jsonRaw = bufferedReader.lines().collect(Collectors.joining("\n"));
                filePrinter.getPrintWriter().println(jsonRaw);
                Gson gson = new Gson();
                Cords cords = gson.fromJson(jsonRaw, Cords.class);
                storeCords(cords);
                System.out.println("Status: 200 OK");
                System.out.println("Content-Type: text/plain");
                System.out.println();
                System.out.println(checkPointInTheArea(cords));
            }
        }
    }

    private static void storeCords(Cords cords){
        if (history.size() >= HISTORY_SIZE) {
            history.removeFirst(); 
        }
        history.addLast(cords);
    }

    private static boolean checkPointInTheArea(Cords cords){
        int x = cords.getX();
        int y = cords.getY();
        int R = cords.getR();
        boolean circleCond = (x >= 0 && y >= 0 && x * x + y * y <= R*R/4);
        boolean triangleCond = (x <= 0 && y <= 0 && x >= -R && y >= -R && y >= x - R);
        boolean squareCond = (x >= 0 && y <= 0 && x <= R && y >= -R);
        return circleCond || triangleCond || squareCond;
    }
}
