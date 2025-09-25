package com.infernumvii.listener;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.infernumvii.annotation.Request;
import com.infernumvii.annotation.model.Method;
import com.infernumvii.controller.TableController;
import com.infernumvii.fcgi.FCGIContext;
import com.infernumvii.fcgi.FCGIWriteContext;
import com.infernumvii.http.ContentType;
import com.infernumvii.http.Response;
import com.infernumvii.http.StatusCode;

public class RequestListener {
    private TableController tableController = null;
    private FCGIContext fcgiRequest = null;
    private FCGIWriteContext out = null;
    private String body;
    private long startTime;
    

    public RequestListener() {
    }

    public RequestListener(TableController tableController, FCGIContext fcgiRequest, FCGIWriteContext writeContext, long startTime){
        this.tableController = tableController;
        this.fcgiRequest = fcgiRequest;
        this.out = writeContext;
        body = new String(fcgiRequest.getStdinData().array(), StandardCharsets.UTF_8);
        this.startTime = startTime;
    }



    

    @Request(method = Method.POST)
    public void onPost() throws IOException{
        try {
            String answer = tableController.storeRowAndReturnAllTable(body, startTime);
            out.write(
                new Response.Builder()
                .withStatusCode(StatusCode.C_200)
                .withContentType(ContentType.TEXT_HTML)
                .withBody(answer)
                .build()
                .toString()
            );
        } catch (Exception e) {
            out.write(
                new Response.Builder()
                .withStatusCode(StatusCode.C_400)
                .withContentType(ContentType.TEXT_PLAIN)
                .withBody(e.getMessage())
                .build()
                .toString());
        }
        
    }

    @Request(method = Method.ANY)
    public void onAny() throws IOException{
        out.write(
            new Response.Builder()
            .withStatusCode(StatusCode.C_200)
            .withContentType(ContentType.TEXT_PLAIN)
            .withBody("Method is not allowed")
            .build()
            .toString()
        );
    }

}
