package com.example.warehouse.exceptions;

public class MaterialTypeNotFoundException extends RuntimeException {
    public MaterialTypeNotFoundException(String message) {
        super(message);
    }
}