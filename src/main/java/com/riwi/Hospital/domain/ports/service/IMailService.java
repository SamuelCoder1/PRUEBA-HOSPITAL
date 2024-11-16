package com.riwi.Hospital.domain.ports.service;

import com.riwi.Hospital.application.dtos.requests.EmailRequest;

public interface IMailService {


    //GENERICO
    void sendEmail(EmailRequest emailRequest);

    //void sendReport(Carga carga, ReportRequest request);

}