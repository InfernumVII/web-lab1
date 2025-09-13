package com.infernumvii.listener;

import com.fastcgi.FCGIInterface;
import com.infernumvii.Main;
import com.infernumvii.annotation.Request;
import com.infernumvii.annotation.model.Method;
import com.infernumvii.controller.TableController;
import com.infernumvii.http.ContentType;
import com.infernumvii.http.Response;
import com.infernumvii.http.StatusCode;

public class RequestListener {
    private static final TableController tableController = new TableController();

    @Request(method = Method.POST)
    public void onPost(String body){
        try {
            String answer = tableController.storeRowAndReturnAllTable(body);
            System.out.println(
                new Response.Builder()
                .withStatusCode(StatusCode.C_200)
                .withContentType(ContentType.APPLICATION_JSON)
                .withBody(answer)
                .build()
                .toString()
            );
        } catch (Exception e) {
            System.out.println(
                new Response.Builder()
                .withStatusCode(StatusCode.C_400)
                .withContentType(ContentType.TEXT_PLAIN)
                .withBody(e.getMessage())
                .build()
                .toString());
        }
        
    }

    @Request(method = Method.ANY)
    public void onAny(String body){
        System.out.println(
            new Response.Builder()
            .withStatusCode(StatusCode.C_405)
            .withContentType(ContentType.TEXT_PLAIN)
            .withBody("Method is not allowed")
            .build()
            .toString()
        );
    }
}
