package com.infernumvii.listener;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.stream.Collectors;

import com.infernumvii.fastcgi.FCGIRequest;
import com.infernumvii.annotation.Request;
import com.infernumvii.annotation.model.Method;
import com.infernumvii.controller.TableController;
import com.infernumvii.http.ContentType;
import com.infernumvii.http.Response;
import com.infernumvii.http.StatusCode;

public class RequestListener {
    private TableController tableController = null;
    private FCGIRequest fcgiRequest = null;
    private PrintStream out = null;
    private String body;
    private long startTime;
    

    public RequestListener() {
    }

    public RequestListener(TableController tableController, FCGIRequest fcgiRequest, long startTime){
        this.tableController = tableController;
        this.fcgiRequest = fcgiRequest;
        body = getBody();
        System.out.println(body);
        this.startTime = startTime;
        out = new PrintStream(new BufferedOutputStream(fcgiRequest.outStream, 8192), true);
    }

    private String getBody() {
        try {
            String CONTENT_LENGTH = fcgiRequest.params.getProperty("CONTENT_LENGTH");
            if (CONTENT_LENGTH == null) {
                return "";
            }
            int contentLength = Integer.parseInt(CONTENT_LENGTH);
            byte[] bytes;
            bytes = fcgiRequest.inStream.readNBytes(contentLength);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(byteArrayInputStream));
            return bufferedReader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    

    @Request(method = Method.POST)
    public void onPost(){
        try {
            String answer = tableController.storeRowAndReturnAllTable(body, startTime);
            out.println(
                new Response.Builder()
                .withStatusCode(StatusCode.C_200)
                .withContentType(ContentType.TEXT_HTML)
                .withBody(answer)
                .build()
                .toString()
            );
        } catch (Exception e) {
            out.println(
                new Response.Builder()
                .withStatusCode(StatusCode.C_400)
                .withContentType(ContentType.TEXT_PLAIN)
                .withBody(e.getMessage())
                .build()
                .toString());
        }
        
    }

    @Request(method = Method.ANY)
    public void onAny(){
        out.println(
            new Response.Builder()
            .withStatusCode(StatusCode.C_200)
            .withContentType(ContentType.TEXT_PLAIN)
            .withBody("Method is not allowed")
            .build()
            .toString()
        );
    }

}
