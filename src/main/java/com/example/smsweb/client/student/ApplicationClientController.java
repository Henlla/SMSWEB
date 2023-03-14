package com.example.smsweb.client.student;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequestMapping("student/application")
@Slf4j
public class ApplicationClientController {
    private final String APPLICATION_URL = "http://localhost:8080/api/application/";
    private final String APPLICATION_TYPE_URL = "http://localhost:8080/api/application_type/";
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private final String PROFILE_URL = "http://localhost:8080/api/profiles/";
    public RestTemplate restTemplate;

    @GetMapping("/index")
    public Object index(@CookieValue(name = "_token") String _token) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                return "student/application";
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/sendIndex")
    public Object SendApplicationIndex(@CookieValue(name = "_token") String _token, Model model, Authentication auth) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                // Lấy profile
                Account account = (Account) auth.getPrincipal();
                HttpEntity<String> profileRequest = new HttpEntity<>(headers);
                ResponseEntity<Profile> profileResponse = restTemplate.exchange(PROFILE_URL + "get/" + account.getId(), HttpMethod.GET, profileRequest, Profile.class);
                // Lấy student
                HttpEntity<String> studentRequest = new HttpEntity<>(headers);
                ResponseEntity<Student> responseStudent = restTemplate.exchange(STUDENT_URL + "getByProfile/" + profileResponse.getBody().getId(), HttpMethod.GET, studentRequest, Student.class);

                // Lấy application type
                HttpEntity<String> applicationTypeRequest = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseApplicationType = restTemplate.exchange(APPLICATION_TYPE_URL + "list", HttpMethod.GET, applicationTypeRequest, ResponseModel.class);
                String applicationTypeJson = new ObjectMapper().writeValueAsString(responseApplicationType.getBody().getData());
                List<ApplicationType> listApplicationType = new ObjectMapper().readValue(applicationTypeJson, new TypeReference<List<ApplicationType>>() {
                });
                model.addAttribute("student",responseStudent.getBody());
                model.addAttribute("listApplicationType", listApplicationType);
                return "student/sendIndex";
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/get_one_app_type/{id}")
    @ResponseBody
    public Object getOneAppType(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") String id) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if(!isExpired.toLowerCase().equals("token expired")){
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<String>(headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(APPLICATION_TYPE_URL + "findOne/" + id, HttpMethod.GET, request, ResponseModel.class);
                return response.getBody().getData();
            }else{
                return new ResponseEntity<String>(isExpired,HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("GetOne ApplicationType: " + e.getMessage());
            return new ResponseEntity<String>("Don't find any records",HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/sendApplication")
    @ResponseBody
    public Object SendApplication(@CookieValue(name = "_token") String _token, @RequestBody Application application) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                MultiValueMap<String, Application> content = new LinkedMultiValueMap<>();
                content.add("application", application);
                HttpEntity<MultiValueMap<String, Application>> request = new HttpEntity<MultiValueMap<String, Application>>(content, headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(APPLICATION_URL + "save", HttpMethod.POST, request, ResponseModel.class);
                return response.getBody().getData();
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Save Application: " + e.getMessage());
            return new ResponseEntity<String>("Send fail", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getApplicationByStudent")
    @ResponseBody
    public Object GetApplicationByStudent(@CookieValue(name = "_token") String _token, Authentication auth) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);

                // Lấy profile
                Account account = (Account) auth.getPrincipal();
                HttpEntity<String> requestProfile = new HttpEntity<>(headers);
                ResponseEntity<Profile> profile = restTemplate.exchange(PROFILE_URL + "get/" + account.getId(), HttpMethod.GET, requestProfile, Profile.class);
                // Lấy student
                HttpEntity<String> requestStudent = new HttpEntity<>(headers);
                ResponseEntity<Student> responseStudent = restTemplate.exchange(STUDENT_URL + "/getByProfile/" + profile.getBody().getId(), HttpMethod.GET, requestStudent, Student.class);

                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseApplication = restTemplate.exchange(APPLICATION_URL + "finApplicationByStudentId/" + responseStudent.getBody().getId(), HttpMethod.GET, request, ResponseModel.class);
                String applicationJson = new ObjectMapper().writeValueAsString(responseApplication.getBody().getData());
                List<Application> applicationList = new ObjectMapper().readValue(applicationJson, new TypeReference<List<Application>>() {
                });
                return applicationList;
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }
}
