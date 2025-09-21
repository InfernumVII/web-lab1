package com.infernumvii.exception;


public class CordsInvalidFormat extends RuntimeException {
    public CordsInvalidFormat(){
        super("Cords should have valid format");
    }
}
