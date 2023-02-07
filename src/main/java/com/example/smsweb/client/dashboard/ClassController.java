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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
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
    @ResponseBody
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
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        content.add("newClass", newClass);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "save", HttpMethod.POST, request, ResponseModel.class);

        return "success";

    }

    @GetMapping("/dashboard/class-details/{id}")
    @ResponseBody
    public Object class_details(@CookieValue(name = "_token", defaultValue = "") String _token,
                                  @PathVariable("id")Integer id) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<String> response = restTemplate.exchange(CLASS_URL+"findOne/"+id,
                HttpMethod.GET,request,String.class);
        ResponseModel responseModel = objectMapper.readValue(response.getBody(),new TypeReference<ResponseModel>(){});
        String convertToJson = objectMapper.writeValueAsString(responseModel.getData());
        Classses classModel = objectMapper.readValue(convertToJson,Classses.class);
        return classModel;
    }

    @PostMapping("/dashboard/class-checkExisted")
    @ResponseBody
    public String checkExistedClass(@CookieValue(name = "_token", defaultValue = "") String _token,
                                    @RequestParam("classCode")String classCode) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (_token == ""){
            return "failed";
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);

        MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        content.add("classCode", classCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
        ResponseEntity<String> response = restTemplate.exchange(CLASS_URL+"findClassCode", HttpMethod.POST, request, String.class);

        return response.toString();
    }

//    @PostMapping("/dashboard/import-excel")
//    public ResponseEntity<ResponseModel> importExcelFile(@RequestParam("file")MultipartFile importFile){
//
//    }

    @GetMapping("/dashboard/classList")
    @ResponseBody
    public HttpEntity<?> getClassList(Model model,@CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers= new HttpHeaders();
        headers.set("Authorization","Bearer "+_token);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(CLASS_URL+"list",HttpMethod.GET,request,String.class);
        List<Classses> classList = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<Classses>>(){});


        model.addAttribute("classes",classList);
        return ResponseEntity.status(HttpStatus.OK).body(classList);
    }
}
