package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.DataNotification;
import com.example.smsweb.dto.MulticastMessageRepresentation;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Application;
import com.example.smsweb.models.Devices;
import com.example.smsweb.models.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.MultipartConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("dashboard/application")
@MultipartConfig
@Slf4j
public class ApplicationController {
    private final String APPLICATION_URL = "http://localhost:8080/api/application/";
    private final String APPLICATION_TYPE_URL = "http://localhost:8080/api/application_type/";
    private final String URL_FCM = "http://localhost:8080/fcm/";
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private RestTemplate restTemplate;

    @GetMapping("/app_index")
    public String regis_index(@CookieValue(name = "_token", defaultValue = "") String _token, Model model) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseApplication = restTemplate.exchange(APPLICATION_URL + "list", HttpMethod.GET, request, ResponseModel.class);
                ResponseEntity<ResponseModel> responseAppType = restTemplate.exchange(APPLICATION_TYPE_URL + "list", HttpMethod.GET, request, ResponseModel.class);
                model.addAttribute("listApplication", responseApplication.getBody().getData());
                model.addAttribute("listAppType", responseAppType.getBody().getData());
                return "dashboard/application/app_index";
            } else {
                return "redirect:/dashboard/login";
            }
        } catch (Exception e) {
            log.error("Index Application: " + e.getMessage());
            return e.getMessage();
        }
    }

    @GetMapping("/get_one_app/{id}")
    @ResponseBody
    public Object getOneApplication(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") int id) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(APPLICATION_URL + "findOne/" + id, HttpMethod.GET, request, ResponseModel.class);
                return response.getBody().getData();
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("GetOne Application: " + e.getMessage());
            return new ResponseEntity<String>("Don't find any records",HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Object update(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestBody Application application) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                ObjectMapper objectMapper = new ObjectMapper();
                MultiValueMap<String, Object> content = new LinkedMultiValueMap<String, Object>();
                content.add("application", application);
                HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(content, headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(APPLICATION_URL + "update", HttpMethod.PUT, request, ResponseModel.class);

                HttpEntity<Object> requestStudent = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseStudent = restTemplate.exchange(STUDENT_URL+"get/"+application.getStudentId(),HttpMethod.GET,requestStudent,ResponseModel.class);
                String json = objectMapper.writeValueAsString(responseStudent.getBody().getData());

                Student student = objectMapper.readValue(json,Student.class);
                String tokenDevices = student.getStudentByProfile().getAccountByAccountId().getAccountDevices().stream().findFirst().get().getDeviceToken();
                List<String> listDeviceToken = new ArrayList<>();
                listDeviceToken.add(tokenDevices);

                MulticastMessageRepresentation message = new MulticastMessageRepresentation();

                DataNotification dataNotification = new DataNotification();
                dataNotification.setContent(application.getResponseNote());
                dataNotification.setAction("Application");

                String jsonData = new ObjectMapper().writeValueAsString(dataNotification);
                message.setTitle("Thông báo gửi đơn");
                message.setData(jsonData);
                message.setRegistrationTokens(listDeviceToken);

                String messageJson = new ObjectMapper().writeValueAsString(message);
                MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
                params.add("message", messageJson);
                HttpEntity<MultiValueMap<String, Object>> requestFCM = new HttpEntity<>(params, headers);
                ResponseEntity<String> responseFCM = restTemplate.exchange(URL_FCM + "clients", HttpMethod.POST, requestFCM, String.class);
                return response.getBody().getData();
            } else {
                return new ResponseEntity<String>(isExpired,HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Update Application: " + e.getMessage());
            return new ResponseEntity<String>("Update fail",HttpStatus.BAD_REQUEST);
        }
    }
}
