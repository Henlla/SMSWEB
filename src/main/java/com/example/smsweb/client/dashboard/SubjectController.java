package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Subject;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("dashboard/subject")
public class SubjectController {

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
            JWTUtils.checkExpired(_token);
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
        } catch (Exception e) {
            return "redirect:/dashboard/login";
        }
    }

    @PostMapping(value = "/post")
    @ResponseBody
    public Object post(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestBody Subject subject) {
        try {
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            content.add("subjectCode", subject.getSubjectCode());
            content.add("subjectName", subject.getSubjectName());
            content.add("fee", String.valueOf(subject.getFee()));
            content.add("semesterId", String.valueOf(subject.getSemesterId()));
            content.add("majorId", String.valueOf(subject.getMajorId()));
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(SUBJECT_URL + "save", HttpMethod.POST, request, ResponseModel.class);
            return response;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") Integer id) {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
        content.add("id", String.valueOf(id));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
        ResponseEntity<String> response = restTemplate.exchange(SUBJECT_URL + "delete/" + id, HttpMethod.DELETE, request, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return "redirect:/dashboard/subject/index";
        } else {
            return "redirect:/dashboard/subject/index";
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Object update(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestBody Subject subject) {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
        content.add("id", String.valueOf(subject.getId()));
        content.add("subjectCode", subject.getSubjectCode());
        content.add("subjectName", subject.getSubjectName());
        content.add("fee", String.valueOf(subject.getFee()));
        content.add("slot", String.valueOf(subject.getSlot()));
        content.add("semesterId", String.valueOf(subject.getSemesterId()));
        content.add("majorId", String.valueOf(subject.getMajorId()));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(SUBJECT_URL + "save", HttpMethod.POST, request, ResponseModel.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        } else {
            return response;
        }
    }

    @GetMapping("/findOne/{id}")
    @ResponseBody
    public Object findOne(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") int id) {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
        content.add("id", String.valueOf(id));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(SUBJECT_URL + "findOne/" + id, HttpMethod.GET, request, ResponseModel.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getData();
        } else {
            return response;
        }
    }
}
