package com.riwi.Hospital.application.services.impl;

import com.riwi.Hospital.application.dtos.requests.EmailRequest;
import com.riwi.Hospital.domain.ports.service.IMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService implements IMailService {

    @Autowired
    private JavaMailSender mailSender;

    /*@Override
    public void sendReport(Carga carga, ReportRequest request) {


        //SE DECLARA SUBJECT Y BODY, ESTO PUEDE SER PERSONALIZADO
        String subject = "Carga damage report: " + carga.getId();
        String body = "Damage has been reported to the cargo with ID: " + carga.getId() +
                "\nDetails: " + request.getDetalles();

        SimpleMailMessage message = new SimpleMailMessage();

        //ARRAY PARA SABER A QUIEN SE ENVIARA
        String[] to = {
                "daviocassio95@gmail.com",
                "samuelparkourito132@gmail.com",
                "osorioemanuel0520@gmail.com"
        };

        //SE CONFIGURA A QUIEN, EL SUBJECT Y EL BODY A ENVIAR
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);


        try {
            //SE ENVIA EL MESSAGE, OSEA, LO QUE SE CONFIGURO ANTES
            mailSender.send(message);
        } catch (MailSendException e) {
            //SE HACE "UN MANEJO DE ERRORES"
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }*/

    @Override
    public void sendEmail(EmailRequest emailRequest) {
        // Método opcional para enviar correos genéricos
    }
}
