package com.riwi.Hospital.application.services.impl;

import com.pusher.rest.Pusher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private Pusher pusher;

    public void sendNotification(String channel, String event, String message) {
        Map<String, String> data = new HashMap<>();
        data.put("message", message);

        pusher.trigger(channel, event, data);
    }
}
