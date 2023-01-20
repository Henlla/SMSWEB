package com.example.smsweb.client.dashboard;

import com.example.smsweb.models.Subject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Controller
public class SubjectController {

    private String GET_ALL_SUBJECT_URL = "http://localhost:8080/api/subject/list";
    private RestTemplate restTemplate;
    List<Subject> listSubject;
    @GetMapping("/subject/create-subject")
    public String index(Model model){
        listSubject = new ArrayList<>();
        restTemplate = new RestTemplate();
        ResponseEntity<List<Subject>> response = restTemplate.exchange(GET_ALL_SUBJECT_URL, HttpMethod.GET, null, new ParameterizedTypeReference<>(){});
        listSubject = response.getBody();
        model.addAttribute("listSubject",listSubject);
        return "dashboard/subject/create-subject";
    }
}
