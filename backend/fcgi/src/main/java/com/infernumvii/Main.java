package com.infernumvii;
import com.infernumvii.controller.FCGIController;

public class Main {
    public static void main(String[] args) {
        FCGIController fcgiController = new FCGIController("9000");
        try {
            fcgiController.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
