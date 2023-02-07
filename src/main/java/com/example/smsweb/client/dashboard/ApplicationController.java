package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;


@Controller
@RequestMapping("dashboard/application")
public class ApplicationController {
    private final String APPLICATION_URL = "http://localhost:8080/api/application/";
    private RestTemplate restTemplate;
    private ResponseModel application;
    @GetMapping("/index")
    public String index(@CookieValue(name = "_token",defaultValue = "")String _token, Model model) {
        if(_token.equals("")){
            return "redirect:/dashboard/login";
        }
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        application = restTemplate.getForObject(APPLICATION_URL +"list",ResponseModel.class);
        model.addAttribute("listApplication",application.getData());
        return "dashboard/application/application_index";
    }
}
