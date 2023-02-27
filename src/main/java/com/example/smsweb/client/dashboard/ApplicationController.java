package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Application;
import jakarta.servlet.annotation.MultipartConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("dashboard/application")
@MultipartConfig
public class ApplicationController {
    private final String APPLICATION_URL = "http://localhost:8080/api/application/";
    private final String APPLICATION_TYPE_URL = "http://localhost:8080/api/application_type/";
    private RestTemplate restTemplate;

    @GetMapping("/app_index")
    public String regis_index(@CookieValue(name = "_token", defaultValue = "") String _token, Model model) {
        try {
            JWTUtils.checkExpired(_token);
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<ResponseModel> responseApplication = restTemplate.exchange(APPLICATION_URL + "list", HttpMethod.GET, request, ResponseModel.class);
            ResponseEntity<ResponseModel> responseAppType = restTemplate.exchange(APPLICATION_TYPE_URL + "list", HttpMethod.GET, request, ResponseModel.class);
            model.addAttribute("listApplication", responseApplication.getBody().getData());
            model.addAttribute("listAppType", responseAppType.getBody().getData());
            return "dashboard/application/app_index";
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("token expired")) {
                return "redirect:/dashboard/login";
            } else {
                return "/error/error";
            }
        }
    }

    @GetMapping("/get_one_app/{id}")
    @ResponseBody
    public Object getOneApplication(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") int id) {
        try {
            JWTUtils.checkExpired(_token);
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(APPLICATION_URL + "findOne/" + id, HttpMethod.GET, request, ResponseModel.class);
            return response.getBody().getData();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/save_app")
    @ResponseBody
    public Object save_app(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestBody Application application) {
        try {
            JWTUtils.checkExpired(_token);
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Application> content = new LinkedMultiValueMap<>();
            content.add("application", application);
            HttpEntity<MultiValueMap<String, Application>> request = new HttpEntity<MultiValueMap<String, Application>>(content, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(APPLICATION_URL + "save", HttpMethod.POST, request, ResponseModel.class);
            return response;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Object update(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestBody Application application) {
        try {
            JWTUtils.checkExpired(_token);
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, Object> content = new LinkedMultiValueMap<String, Object>();
            content.add("application", application);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(content, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(APPLICATION_URL + "update", HttpMethod.PUT, request, ResponseModel.class);
            return response.getBody().getData();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
