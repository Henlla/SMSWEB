package com.example.smsweb.client.dashboard;

import com.example.smsweb.api.di.irepository.ISubject;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Major;
import com.example.smsweb.models.Semester;
import com.example.smsweb.models.Subject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("dashboard/subject")
public class SubjectController {
    @Autowired
    private ISubject service;
    private String SUBJECT_URL = "http://localhost:8080/api/subject/";
    private String MAJOR_URL = "http://localhost:8080/api/major/";
    private String SEMESTER_URL = "http://localhost:8080/api/semester/";
    private RestTemplate restTemplate;
    List<Subject> listSubject;
    List<Major> listMajor;
    List<Semester> listSemester;

    @GetMapping("/index")
    public String index(@CookieValue(name = "_token", defaultValue = "") String _token, Model model) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                listSubject = new ArrayList<>();
                listMajor = new ArrayList<>();
                listSemester = new ArrayList<>();

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);

                //Get Subject
                ResponseEntity<ResponseModel> responseSubject = restTemplate.exchange(SUBJECT_URL + "list", HttpMethod.GET, request, ResponseModel.class);
                String subjectJson = new ObjectMapper().writeValueAsString(responseSubject.getBody().getData());
                listSubject = new ObjectMapper().readValue(subjectJson, new TypeReference<List<Subject>>() {
                });

                //Get Major
                ResponseEntity<ResponseModel> responseMajor = restTemplate.exchange(MAJOR_URL + "list", HttpMethod.GET, request, ResponseModel.class);
                String majorJson = new ObjectMapper().writeValueAsString(responseMajor.getBody().getData());
                listMajor = new ObjectMapper().readValue(majorJson, new TypeReference<List<Major>>() {
                });

                //Get Semester
                ResponseEntity<ResponseModel> responseSemester = restTemplate.exchange(SEMESTER_URL + "list", HttpMethod.GET, request, ResponseModel.class);
                String semesterJson = new ObjectMapper().writeValueAsString(responseSemester.getBody().getData());
                listSemester = new ObjectMapper().readValue(semesterJson, new TypeReference<List<Semester>>() {
                });

                model.addAttribute("listSubject", listSubject.stream().sorted((o1, o2) -> o1.getSemesterId().compareTo(o2.getSemesterId())).toList());
                model.addAttribute("listMajor", listMajor);
                model.addAttribute("listSemester", listSemester.stream().sorted((o1, o2) -> o1.getSemesterCode().compareTo(o2.getSemesterCode())).toList());
                model.addAttribute("subject", new Subject());
                return "dashboard/subject/subject_index";
            } else {
                return "redirect:/dashboard/login";
            }

        } catch (Exception e) {
            log.error("Index Application: " + e.getMessage());
            return e.getMessage();
        }
    }

    @GetMapping("/findSubjectByMajorId/{id}")
    @ResponseBody
    public Object findSubjectByMajorId(@CookieValue(name = "_token") String _token, @PathVariable("id") Integer majorId) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                listSubject = new ArrayList<>();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);

                ResponseEntity<ResponseModel> responseMajor = restTemplate.exchange(SUBJECT_URL + "findByMajorId/" + majorId, HttpMethod.GET, request, ResponseModel.class);
                String majorJson = new ObjectMapper().writeValueAsString(responseMajor.getBody().getData());
                listSubject = new ObjectMapper().readValue(majorJson, new TypeReference<List<Subject>>() {
                });
                return listSubject;
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<String>("Can't get Major", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/save")
    @ResponseBody
    public Object post(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestBody Subject subject) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                MultiValueMap<String, Object> content = new LinkedMultiValueMap<>();
                content.add("subject", subject);
                HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(content, headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(SUBJECT_URL + "save", HttpMethod.POST, request, ResponseModel.class);
                return response.getBody().getData();
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            log.error("Save Subject: " + ex.getMessage());
            return new ResponseEntity<String>("Create fail", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/delete/{id}")
    @ResponseBody
    public Object delete(@CookieValue(name = "_token") String _token, @PathVariable("id") String id) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> request = new HttpEntity<String>(headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(SUBJECT_URL + "delete/" + id, HttpMethod.DELETE, request, ResponseModel.class);
                return response.getBody().getData();
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Delete Subject: " + e.getMessage());
            return new ResponseEntity<String>("Delete fail", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Object update(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestBody Subject subject) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                MultiValueMap<String, Object> content = new LinkedMultiValueMap<>();
                content.add("subject", subject);
                HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(content, headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(SUBJECT_URL + "update", HttpMethod.PUT, request, ResponseModel.class);
                return response.getBody().getData();
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Update Subject: " + e.getMessage());
            return new ResponseEntity<String>("Update fail", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/findOne/{id}")
    @ResponseBody
    public Object findOne(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") int id) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
                content.add("id", String.valueOf(id));
                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(SUBJECT_URL + "findOne/" + id, HttpMethod.GET, request, ResponseModel.class);
                return response.getBody().getData();
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("FindOne Subject: " + e.getMessage());
            return new ResponseEntity<String>("Don't find any records", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/importExcelData")
    @ResponseBody
    public Object importExcelData(@CookieValue("_token") String _token, @RequestParam("file") MultipartFile file) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                String status = service.importExcelData(file);
                if (status.equals("")) {
                    return "Import success";
                } else {
                    return new ResponseEntity<String>(status, HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Import Subject: " + e.getMessage());
            return new ResponseEntity<String>("Import fail", HttpStatus.BAD_REQUEST);
        }
    }

}
