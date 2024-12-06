package com.riwi.Hospital.infrastructure.config;

import com.pusher.rest.Pusher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PusherConfig {

    @Bean
    public Pusher pusher() {
        return new Pusher("1907448", "54c36dfac57463cc869e", "dbef0510b76f26589836");
    }
}
