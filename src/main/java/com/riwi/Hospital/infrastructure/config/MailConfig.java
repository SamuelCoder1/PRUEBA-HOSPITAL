package com.riwi.Hospital.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender getJavaMailSender() {

        //CONFIGURACIONES DE SMTP
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        //CREDENCIALES DE MI CUENTA
        mailSender.setUsername("echeverrysamuel74@gmail.com");
        mailSender.setPassword("sdkwqwvrbqbwqiwv"); // CONTRASEÑA CREADA EN GOOGLE (CONTRASEÑAS DE APLICACION) EN GOOGLE

        //PROPIEDADES DE MAILSENDER
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Asegúrate de que esto esté aquí
        props.put("mail.smtp.starttls.required", "true"); // Y esto también
        props.put("mail.debug", "true");

        return mailSender;
    }
}
