package com.example.smsweb.client.teacher;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/teacher")
public class HomeController {
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private final String PROFILE_URL = "http://localhost:8080/api/profiles/";
    private final String MAJOR_URL = "http://localhost:8080/api/major/";
    private final String SUBJECT_URL = "http://localhost:8080/api/subject/";
    private final String SEMESTER_URL = "http://localhost:8080/api/semester/";
    private final String NEWS_URL = "http://localhost:8080/api/news/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping(value = {"/index", ""})
    public String index(Model model) {
//        try {
//            JWTUtils.checkExpired(_token);
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers= new HttpHeaders();
//            headers.set("Authorization","Bearer "+_token);
//            HttpEntity<Object> request = new HttpEntity<>(headers);
//            ResponseEntity<String> response = restTemplate.exchange(PROFILE_URL+"get/"+_accountId,HttpMethod.GET,request,String.class);
//            Profile profile = new ObjectMapper().readValue(response.getBody(), new TypeReference<Profile>(){});
//
//            model.addAttribute("profile", profile);
//            return "teacherDashboard/home";
//        }catch (Exception ex){
//            log.error(ex.getMessage());
//            return "redirect:/teacherDashboard/logout";
//        }
        return "teacherDashboard/home";
    }

    @GetMapping("/students")
    public String students(Model model,@CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers= new HttpHeaders();
            headers.set("Authorization","Bearer "+_token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(STUDENT_URL+"list",HttpMethod.GET,request,String.class);
            List<Student> listStudent = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<Student>>(){});
            model.addAttribute("students",listStudent.stream().sorted((s1,s2)->s2.getId().compareTo(s1.getId())).toList());
            return "teacherDashboard/student/student_index";
        }catch (Exception ex){
            log.error(ex.getMessage());
            return "redirect:/teacherDashboard/logout";
        }
    }


    @GetMapping("/majors")
    public String majors(@CookieValue(name = "_token", defaultValue = "") String _token, Model model) {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ResponseModel responseModel = new ResponseModel();
            HttpHeaders headers = new HttpHeaders();
            responseModel = restTemplate.getForObject(MAJOR_URL + "list", ResponseModel.class);
            model.addAttribute("listMajor", responseModel.getData());
            return "teacherDashboard/major/marjor_index";
        } catch (Exception e) {
            return "redirect:/teacherDashboard/login";
        }
    }

    @GetMapping("/subjects")
    public String subjects(@CookieValue(name = "_token", defaultValue = "") String _token, Model model) {
        try {
            JWTUtils.checkExpired(_token);
            ResponseModel listSubject = new ResponseModel();
            ResponseModel listMajor = new ResponseModel();
            ResponseModel listSemester = new ResponseModel();
            RestTemplate restTemplate = new RestTemplate();

            listSubject = restTemplate.getForObject(SUBJECT_URL + "list", ResponseModel.class);
            listMajor = restTemplate.getForObject(MAJOR_URL + "list", ResponseModel.class);
            listSemester = restTemplate.getForObject(SEMESTER_URL + "list", ResponseModel.class);

            model.addAttribute("listSubject", listSubject.getData());
            model.addAttribute("listMajor", listMajor.getData());
            model.addAttribute("listSemester", listSemester.getData());
            model.addAttribute("subject", new Subject());
            return "teacherDashboard/subject/subject_index";
        } catch (Exception e) {
            return "redirect:/teacherDashboard/login";
        }
    }

    @GetMapping("/news")
    public String news(Model model,
                        @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ResponseModel> response = restTemplate.getForEntity(NEWS_URL + "list", ResponseModel.class);
            String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
            List<News> newsList = new ObjectMapper().readValue(json, new TypeReference<List<News>>() {});
            model.addAttribute("news", newsList);
            return "teacherDashboard/news/new_index";
        }catch (Exception ex){
            log.error(ex.getMessage());
            return "redirect:/teacherDashboard/logout";
        }
    }

    @GetMapping("/classes")
    public String classes(Model model, @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {

        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "list", HttpMethod.GET, request, String.class);
        List<Classses> classList = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<Classses>>() {
        });


        model.addAttribute("classes", classList);
        return "teacherDashboard/class/class_index";
    }

    @GetMapping("class/{classCode}")
    public String class_details(Model model, @CookieValue(name = "_token", defaultValue = "") String _token,
                                @PathVariable("classCode") String classCode) {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("classCode", classCode);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "findClassCode", HttpMethod.POST, request, ResponseModel.class);
            String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
            Classses classses = new ObjectMapper().readValue(json, Classses.class);
            model.addAttribute("class", classses);
            return "teacherDashboard/class/class_details";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return "redirect:/teacherDashboard/logout";
        }
    }
}
