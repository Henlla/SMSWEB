package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class ClassController {

    private String MAJOR_URL = "http://localhost:8080/api/major/";
    private String CLASS_URL = "http://localhost:8080/api/classes/";
    private final String TEACHER_URL = "http://localhost:8080/api/teachers/";


    @GetMapping("/dashboard/class-index")
    public String index(Model model,@CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(CLASS_URL+"list",HttpMethod.GET,request,String.class);
        List<Classses> classList = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<Classses>>(){});


        model.addAttribute("classes",classList);
        return "dashboard/class/class_index";
    }


    @GetMapping("/dashboard/class-create")
    public String createClass(Model model,@CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> teacherResponse = restTemplate.exchange(TEACHER_URL+"list",HttpMethod.GET,request,String.class);
        List<Teacher> teacherList = new ObjectMapper().readValue(teacherResponse.getBody(), new TypeReference<List<Teacher>>(){});

        ResponseModel listMajor = restTemplate.getForObject(MAJOR_URL + "list", ResponseModel.class);

        model.addAttribute("majors", listMajor.getData());
        model.addAttribute("teachers",teacherList);
        return "dashboard/class/class_create";
    }

    @PostMapping("/dashboard/class-create")
    public String createClassby(Model model,
                                @CookieValue(name = "_token", defaultValue = "") String _token,
                                @RequestParam("newClass")String newClass
    ) throws JsonProcessingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);

        MultiValueMap<String, String> content = new LinkedMultiValueMap<>();

        content.add("newClass", newClass);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "save", HttpMethod.POST, request, ResponseModel.class);

        return response.toString();
    }

    @PostMapping("/dashboard/class-create/a")
    public String createClassa(Model model,
                              @CookieValue(name = "_token", defaultValue = "") String _token,
                              @RequestParam("classCode")String classCode,
                              @RequestParam("majorId")String majorId,
                              @RequestParam("teacherId")String teacherId,
                              @RequestParam("limitStudent")String limitStudent
    ) throws JsonProcessingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);

        MultiValueMap<String, String> content = new LinkedMultiValueMap<>();

        content.add("classCode", classCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "save", HttpMethod.POST, request, ResponseModel.class);

        return response.toString();
    }

}
