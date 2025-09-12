package com.infernumvii.exception;

import com.infernumvii.model.Cords;

public class CordsInvalidFormat extends RuntimeException {
    public CordsInvalidFormat(){
        super("Cords should have valid format");
    }
}
