package org.infernumvii;


public enum ContentType {
    APPLICATION_JSON("application/json"),
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html");

    private final String value;

    ContentType(String value){
        this.value = value;
    }

    public String toString(){
        return String.format("Content-Type: %s", value);
    }
}
