package com.infernumvii.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import com.infernumvii.fastcgi.FCGIInterface;
import com.infernumvii.processor.FCGIProcessor;


public class FCGIController {
    private FCGIInterface fcgiInterface = new FCGIInterface();

    public FCGIController(String port){
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
