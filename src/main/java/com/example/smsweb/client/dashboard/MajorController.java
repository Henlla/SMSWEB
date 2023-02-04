package com.example.smsweb.client.dashboard;

import com.example.smsweb.api.di.repository.MajorRepository;
import com.example.smsweb.dto.ResponseModel;
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

@Controller
@RequestMapping("dashboard/major")
public class MajorController {
    @Autowired
    public MajorRepository dao;
    private final String MAJOR_URL = "http://localhost:8080/api/major/";
    ResponseModel listMajor;
    RestTemplate restTemplate;

    @GetMapping("/index")
    public String index(@CookieValue(name = "_token", defaultValue = "") String _token, Model model) {
        if (_token.equals("")) {
            return "redirect:dashboard/login";
        }
        restTemplate = new RestTemplate();
        listMajor = new ResponseModel();
        HttpHeaders headers = new HttpHeaders();
        listMajor = restTemplate.getForObject(MAJOR_URL + "list", ResponseModel.class);
        model.addAttribute("listMajor", listMajor.getData());
        model.addAttribute("major", new Major());
        return "dashboard/major/major_index";
    }

    @PostMapping("/post")
    @ResponseBody
    public Object post(@RequestBody Major major) {
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
        content.add("majorCode", major.getMajorCode());
        content.add("majorName", major.getMajorName());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(MAJOR_URL + "save", HttpMethod.POST, request, ResponseModel.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        } else {
            return response;
        }
    }

    @GetMapping("/edit/{id}")
    @ResponseBody
    public Object edit(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") int id) {
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(MAJOR_URL + "fineOne/" + id, HttpMethod.GET, null, ResponseModel.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getData();
        } else {
            return response;
        }
    }

    @GetMapping("/findOne/{id}")
    @ResponseBody
    public Object findOne(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") int id) {
        if (_token.isEmpty()) {
            return "redirect:/dashboard/login";
        } else {
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            content.add("id", String.valueOf(id));
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(MAJOR_URL + "findOne/" + id, HttpMethod.GET, request, ResponseModel.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody().getData();
            } else {
                return response;
            }
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Object update(@RequestBody Major major) {
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
        content.add("id", String.valueOf(major.getId()));
        content.add("majorCode", major.getMajorCode());
        content.add("majorName", major.getMajorName());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(MAJOR_URL + "save", HttpMethod.POST, request, ResponseModel.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        } else {
            return response;
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
        content.add("id", String.valueOf(id));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
        ResponseEntity<String> response = restTemplate.exchange(MAJOR_URL + "delete/" + id, HttpMethod.DELETE, request, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return "redirect:/major/index";
        } else {
            return "redirect:/major/index";
        }
    }
}
