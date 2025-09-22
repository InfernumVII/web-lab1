package com.infernumvii;


import com.infernumvii.annotation.*;
import com.infernumvii.annotation.model.Method;
import com.infernumvii.http.*;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class ReactiveFCGIRequestListener {
    FCGIContext fcgiContext;
    
    public ReactiveFCGIRequestListener() {
    }
    
    public ReactiveFCGIRequestListener(FCGIContext fcgiContext) {
        this.fcgiContext = fcgiContext;
    }
    
    @Request(method = Method.POST)
    public Mono<Void> onPost() {
        return Mono.fromCallable(() -> {
            try {
                System.out.println("Processing POST with body: " + fcgiContext.getBody());
                String answer = fcgiContext.getTableController().storeRowAndReturnAllTable(fcgiContext.getBody(), 0);
                
                String response = new Response.Builder()
                    .withStatusCode(StatusCode.C_200)
                    .withContentType(ContentType.TEXT_HTML)
                    .withBody(answer)
                    .build()
                    .toString();
                
                fcgiContext.getOut().println(response);
                System.out.println("POST response sent");
                
            } catch (Exception e) {
                e.printStackTrace();
                String errorResponse = new Response.Builder()
                    .withStatusCode(StatusCode.C_400)
                    .withContentType(ContentType.TEXT_PLAIN)
                    .withBody(e.getMessage())
                    .build()
                    .toString();
                
                fcgiContext.getOut().println(errorResponse);
            }
            return null;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }
    
    @Request(method = Method.ANY)
    public Mono<Void> onAny() {
        return Mono.fromCallable(() -> {
            String response = new Response.Builder()
                .withStatusCode(StatusCode.C_200)
                .withContentType(ContentType.TEXT_PLAIN)
                .withBody("Method is not allowed")
                .build()
                .toString();
            
            fcgiContext.getOut().println(response);
            System.out.println("ANY response sent");
            return null;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }
}