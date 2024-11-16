package com.riwi.Hospital.application.dtos.exception;

public class PdfGenerationException extends RuntimeException {
    public PdfGenerationException(String message) {
        super(message);
    }
}