package com.riwi.Hospital.application.dtos.requests;


import lombok.Data;

@Data
public class EmailRequest {
    private String to;        // Dirección de correo electrónico del destinatario
    private String subject;   // Asunto del correo
    private String message;      // Cuerpo del correo
}

