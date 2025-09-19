package com.infernumvii.annotation.model;

public enum Method {
    GET("GET"),
    POST("POST"),
    ANY("*");

    private final String name;

    private Method(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
