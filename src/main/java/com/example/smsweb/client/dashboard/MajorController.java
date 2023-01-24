package com.example.smsweb.client.dashboard;

import com.example.smsweb.api.di.repository.MajorRepository;
import com.example.smsweb.models.Major;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("major")
public class MajorController {
    @Autowired
    public MajorRepository dao;
    private final String MAJOR_URL = "http://localhost:8080/api/major/";
    List<Major> listMajor;
    RestTemplate restTemplate;

    @GetMapping("/index")
    public String index(Model model) {
        restTemplate = new RestTemplate();
        listMajor = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        listMajor = restTemplate.getForObject(MAJOR_URL+"list", ArrayList.class);
        model.addAttribute("listMajor",listMajor);
        model.addAttribute("major",new Major());
        return "dashboard/major/major_index";
    }

    @PostMapping("/post")
    @ResponseBody
    public String post(@RequestBody Major major){
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String,String> content = new LinkedMultiValueMap<>();
        content.add("majorCode",major.getMajorCode());
        content.add("majorName",major.getMajorName());
        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(content,headers);
        ResponseEntity<String> response = restTemplate.exchange(MAJOR_URL +"save",HttpMethod.POST,request,String.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }else {
            return response.getBody();
        }
    }
}
