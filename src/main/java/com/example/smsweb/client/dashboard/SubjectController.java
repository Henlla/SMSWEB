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
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/save")
    @ResponseBody
    public Object post(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestBody Subject subject) {
        try {
            JWTUtils.checkExpired(_token);
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, Object> content = new LinkedMultiValueMap<>();
            content.add("subject", subject);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(content, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(SUBJECT_URL + "save", HttpMethod.POST, request, ResponseModel.class);
            return response;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") Integer id) {
        try {
            JWTUtils.checkExpired(_token);
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            content.add("id", String.valueOf(id));
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
            ResponseEntity<String> response = restTemplate.exchange(SUBJECT_URL + "delete/" + id, HttpMethod.DELETE, request, String.class);
            return response.getBody().toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Object update(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestBody Subject subject) {
        try {
            JWTUtils.checkExpired(_token);
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> content = new LinkedMultiValueMap<>();
            content.add("subject", subject);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(content, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(SUBJECT_URL + "update", HttpMethod.PUT, request, ResponseModel.class);
            return response;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/findOne/{id}")
    @ResponseBody
    public Object findOne(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") int id) {
        try {
            JWTUtils.checkExpired(_token);
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            content.add("id", String.valueOf(id));
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(SUBJECT_URL + "findOne/" + id, HttpMethod.GET, request, ResponseModel.class);
            return response.getBody().getData();
        }catch (Exception e){
            return e.getMessage();
        }
    }

    @PostMapping("/importExcelData")
    @ResponseBody
    public Object importExcelData(@RequestParam("file")MultipartFile file){
        return "";
    }
}
