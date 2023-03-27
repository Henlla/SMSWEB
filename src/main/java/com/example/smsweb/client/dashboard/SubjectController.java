package com.example.smsweb.client.dashboard;

import com.example.smsweb.api.di.irepository.ISubject;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Subject;
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
    ResponseModel listSubject;
    ResponseModel listMajor;
    ResponseModel listSemester;

    @GetMapping("/index")
    public String index(@CookieValue(name = "_token", defaultValue = "") String _token, Model model) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                listSubject = new ResponseModel();
                listMajor = new ResponseModel();
                listSemester = new ResponseModel();
                restTemplate = new RestTemplate();

                listSubject = restTemplate.getForObject(SUBJECT_URL + "list", ResponseModel.class);
                listMajor = restTemplate.getForObject(MAJOR_URL + "list", ResponseModel.class);
                listSemester = restTemplate.getForObject(SEMESTER_URL + "list", ResponseModel.class);

                model.addAttribute("listSubject", listSubject.getData());
                model.addAttribute("listMajor", listMajor.getData());
                model.addAttribute("listSemester", listSemester.getData());
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
