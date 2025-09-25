package com.infernumvii;

import java.io.IOException;

import com.infernumvii.fcgi.FCGIServer;

public class Main {
    public static void main(String[] args) throws IOException {
        FCGIServer fcgiServer = new FCGIServer();
        try {
            fcgiServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
