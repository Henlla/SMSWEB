package com.example.smsweb.task;

import com.example.smsweb.dto.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class ScheduleTask {
    private final String URL_FCM = "http://localhost:8080/fcm/";

    @Scheduled(cron = "0 03 20 1/1 * *")
    public void sendNotification() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("message", "Test");
        params.add("title", "Test fcm");
        params.add("registrationToken", "flZFd9llSFSdsxD68rVkOi:APA91bE7CEdeJtS4GNC_JbTnjprL5AwHzZvIlWq399PPr__t9uVq0ThoUOAkbCIOzdIqNZaqhE19oUGe0E5iyzc8qGhGhJYA5tVektjojnTQaPe4rd0urta0LBH78oppT_xRu7CO_j69");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(URL_FCM + "client", HttpMethod.POST, request, String.class);
    }

}
