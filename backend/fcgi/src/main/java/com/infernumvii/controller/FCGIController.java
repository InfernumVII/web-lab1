package com.infernumvii.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.infernumvii.fastcgi.FCGIInterface;
import com.infernumvii.processor.FCGIProcessor;
import com.infernumvii.*;


public class FCGIController {
    private String port = "9000";
    private FCGIInterface fcgiInterface = new FCGIInterface();

    public FCGIController(String port){
        this.port = port;
        System.setProperty("FCGI_PORT", port);
    }

    public void start() throws IOException{
    System.out.println("Started java!!!!!");
        while (fcgiInterface.FCGIaccept() >= 0) {
            try {
                FCGIProcessor.process();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
