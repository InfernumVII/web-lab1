package com.infernumvii.http;

public enum StatusCode {
    C_200(200, "OK"),
    C_405(405, "Method Not Allowed"),
    C_400(400, "Bad Request"),
    C_500(500, "Internal Server Error");

    private final int stateClass;
    private final String explain;

    StatusCode(int stateClass, String explain){
        this.stateClass = stateClass;
        this.explain = explain;
    }

    @Override
    public String toString(){
        return String.format("Status: %d %s", stateClass, explain);
    }
}
