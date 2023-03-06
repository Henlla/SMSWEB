package com.example.smsweb.task;
import com.example.smsweb.dto.DataNotification;
import com.example.smsweb.dto.MulticastMessageRepresentation;
import com.example.smsweb.models.Devices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ScheduleTask {
    private final String URL_FCM = "http://localhost:8080/fcm/";
    private final String URL_DEVICE = "http://localhost:8080/api/device/";

    @Scheduled(cron = "0 44 17 1/1 * *")
    public void sendNotification() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        LocalDate currenDate = LocalDate.now();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String devices = restTemplate.getForObject(URL_DEVICE+"getAll", String.class);
        List<Devices> devicesList = new ObjectMapper().readValue(devices, new TypeReference<List<Devices>>() {
        });

        List<String> listDeviceToken = new ArrayList<>();
        for (Devices device : devicesList){
            listDeviceToken.add(device.getDeviceToken());
        }

        MulticastMessageRepresentation message = new MulticastMessageRepresentation();
        DataNotification dataNotification = new DataNotification();
        dataNotification.setContent(currenDate.toString());
        dataNotification.setAction("Schedule");

        String jsonData = new ObjectMapper().writeValueAsString(dataNotification);
        message.setTitle("Lịch học hôm nay");
        message.setData(jsonData);
        message.setRegistrationTokens(listDeviceToken);
        String messageJson = new ObjectMapper().writeValueAsString(message);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        params.add("message", messageJson);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(URL_FCM + "clients", HttpMethod.POST, request, String.class);
        log.info("do task");
    }

}
