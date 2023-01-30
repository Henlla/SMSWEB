package com.example.smsweb.client.dashboard;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller("dashboard")
public class DashboardController {
    private final String URL_ACCOUNT = "http://localhost:8080/api/accounts/";
    RestTemplate restTemplate;

    @GetMapping("/dashboard")
    public String index() {
        return "dashboard/index";
    }

    @GetMapping("/loginIndex")
    public String loginIndex() {
        return "dashboard/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
        content.add("username", username);
        content.add("passwrod", password);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
        ResponseEntity<String> response = restTemplate.exchange(URL_ACCOUNT + "login", HttpMethod.POST, request, String.class);
        return response.getBody();
    }
}
